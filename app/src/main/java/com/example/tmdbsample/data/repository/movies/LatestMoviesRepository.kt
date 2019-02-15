package com.example.tmdbsample.data.repository.movies

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.data.local.db.MoviesDatabase
import com.example.tmdbsample.data.local.db.dao.ShortMoviesDao
import com.example.tmdbsample.data.local.db.mapper.ShortMovieApiToShortMovieEntityMapper
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.local.source.PagesSource
import com.example.tmdbsample.data.network.api.MoviesApi
import com.example.tmdbsample.data.network.model.PagedResult
import com.example.tmdbsample.data.network.model.ShortMovieApi
import com.example.tmdbsample.data.repository.FetchPageTask
import com.example.tmdbsample.data.repository.NetworkBoundResource
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.testing.OpenForTesting
import com.example.tmdbsample.utils.AppExecutors
import com.example.tmdbsample.utils.toLiveData
import io.reactivex.Single
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OpenForTesting
class LatestMoviesRepository(
    private val moviesApi: MoviesApi,
    private val db: MoviesDatabase,
    private val moviesDao: ShortMoviesDao,
    private val pagesSource: PagesSource,
    private val executors: AppExecutors
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // 2019-01-01

    fun getMovies(
        startDate: Date,
        endDate: Date,
        refresh: Boolean = false
    ): LiveData<Result<List<ShortMovieEntity>>> {

        return object :
            NetworkBoundResource<List<ShortMovieEntity>, PagedResult<ShortMovieApi>>(executors) {
            override fun saveCallResult(item: PagedResult<ShortMovieApi>) {
                val itemsApi = item.results

                val pageInfo = PageInfo.create(item.page, item.total)
                Timber.d("saveCallResult: pageInfo $pageInfo itemsApi: ${itemsApi?.size}")

                if (itemsApi != null) {
                    val entities = itemsApi.map { ShortMovieApiToShortMovieEntityMapper.apply(it) }
                    db.runInTransaction {
                        // clear table when first call success
                        moviesDao.clear()

                        moviesDao.insert(entities)
                        pagesSource.setLatestMoviesMatchesPage(pageInfo)
                    }
                }
            }

            override fun shouldFetch(data: List<ShortMovieEntity>?) =
                data?.isEmpty() == true || refresh

            override fun loadFromDb(): LiveData<List<ShortMovieEntity>> = moviesDao.getMovies()

            override fun createCall(): LiveData<Result<PagedResult<ShortMovieApi>>> {
                val startDateStr = dateFormat.format(startDate)
                val endDateStr = dateFormat.format(endDate)
                return moviesApi.getMovies(startDateStr, endDateStr, 1).toLiveData()
            }
        }.asLiveData()
    }

    fun loadNextPage(startDate: Date, endDate: Date): LiveData<Result<Boolean>> {
        Timber.d("loadNextPage: ")
        return object : FetchPageTask<PagedResult<ShortMovieApi>>(executors) {
            override fun getCurrentPage() = pagesSource.getLatestMoviesPage()

            override fun getDataFromApi(page: Int): Single<PagedResult<ShortMovieApi>> {
                val startDateStr = dateFormat.format(startDate)
                val endDateStr = dateFormat.format(endDate)
                return moviesApi.getMovies(startDateStr, endDateStr, page)
            }

            override fun handleSuccessResponse(response: PagedResult<ShortMovieApi>): Boolean {
                val items = response.results
                val pageInfo = PageInfo.create(response.page, response.total)

                if (items != null && items.isNotEmpty()) {
                    val visitedEntityList = items
                        .map { ShortMovieApiToShortMovieEntityMapper.apply(it) }

                    db.runInTransaction {
                        moviesDao.insert(visitedEntityList)
                        pagesSource.setLatestMoviesMatchesPage(pageInfo)
                    }
                }
                return pageInfo.hasNextPage()
            }
        }.asLiveData()
    }
}