package com.example.tmdbsample.data.repository.movies

import android.arch.lifecycle.MutableLiveData
import com.example.tmdbsample.data.local.db.MoviesDatabase
import com.example.tmdbsample.data.local.db.dao.ShortMoviesDao
import com.example.tmdbsample.data.local.db.mapper.ShortMovieApiToShortMovieEntityMapper
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.local.source.PagesSource
import com.example.tmdbsample.data.network.api.MoviesApi
import com.example.tmdbsample.data.network.model.PagedResult
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.global.BaseTest
import com.example.tmdbsample.utils.InstantAppExecutors
import com.example.tmdbsample.utils.LiveDataTestUtil.getValue
import com.example.tmdbsample.utils.TestDataApi
import com.example.tmdbsample.utils.TestDataLocal
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.util.Calendar
import java.util.Date

class LatestMoviesRepositoryTest : BaseTest() {
    private lateinit var repository: LatestMoviesRepository
    private val dao: ShortMoviesDao = mock()
    private val pagesSource: PagesSource = mock()
    private val db = mock<MoviesDatabase>()
    private val api: MoviesApi = mock()

    @Before
    fun init() {
        whenever(db.runInTransaction(any())).thenCallRealMethod()

        repository = LatestMoviesRepository(api, db, dao, pagesSource, InstantAppExecutors())
    }

    @Test
    fun `when server error, Return error and data from db`() {

        val moviesList = TestDataLocal.createShortMovieEntityList(10, 1)
        val moviesLiveData = MutableLiveData<List<ShortMovieEntity>>()
        moviesLiveData.postValue(moviesList)

        val error = Throwable("foo")
        whenever(dao.getMovies()).thenReturn(moviesLiveData)
        whenever(api.getMovies(anyString(), anyString(), anyInt())).thenReturn(
            Single.error(error)
        )

        val value = getValue(repository.getMovies(Date(), Date(), true))

        assertEquals(Result.error(error, moviesList), value)

        Mockito.verify(api).getMovies(anyString(), anyString(), anyInt())
        Mockito.verifyNoMoreInteractions(api)

        Mockito.verify(dao).getMovies()
        Mockito.verifyNoMoreInteractions(dao)
    }

    @Test
    fun `verify date processing for api request`() {

        val moviesLiveData = MutableLiveData<List<ShortMovieEntity>>()
        moviesLiveData.postValue(emptyList())
        whenever(dao.getMovies()).thenReturn(moviesLiveData)

        whenever(api.getMovies(anyString(), anyString(), anyInt())).thenReturn(
            Single.just(PagedResult(0, 0, null))
        )

        val calendar = Calendar.getInstance()

        val expectedStartDate = calendar.apply {
            set(2000, 10, 1) // java Date month 10 is November
        }.time

        val expectedEndDate = calendar.apply {
            set(2000, 10, 29) // java Date month 10 is November
        }.time

        val value = getValue(repository.getMovies(expectedStartDate, expectedEndDate, true))

        Mockito.verify(api).getMovies("2000-11-01", "2000-11-29", 1)
        Mockito.verifyNoMoreInteractions(api)

        Mockito.verify(dao, times(2)).getMovies()
        Mockito.verifyNoMoreInteractions(dao)
    }

    @Test
    fun `when server success, Return success, override db and return data from db`() {
        val moviesList = TestDataLocal.createShortMovieEntityList(10, 0)
        val moviesLiveData = MutableLiveData<List<ShortMovieEntity>>()
        moviesLiveData.postValue(moviesList)

        whenever(dao.getMovies()).thenReturn(moviesLiveData)

        val resultApiUsers = TestDataApi.createShortMovieApiList(20, 0)

        whenever(api.getMovies(anyString(), anyString(), anyInt())).thenReturn(
            Single.just(PagedResult(1, 2, resultApiUsers))
        )

        val page = PageInfo.create(1, 2)
        val movies = resultApiUsers.map { ShortMovieApiToShortMovieEntityMapper.apply(it) }

        whenever(dao.insert(movies)).then {
            moviesLiveData.postValue(movies)
        }

        val date = Calendar.getInstance().apply {
            set(2000, 10, 10)
        }.time
        val value = getValue(repository.getMovies(date, date, true))

        // local data will be overridden by info from api
        assertEquals(Result.success(movies), value)

        // api called to get fresh data
        Mockito.verify(api).getMovies("2000-11-10", "2000-11-10", 1)
        Mockito.verifyNoMoreInteractions(api)

        // dao called to get info once before call, then after call to get updated
        Mockito.verify(dao, times(2)).getMovies()

        // info from api saved into database
        Mockito.verify(dao).clear()
        Mockito.verify(pagesSource).setLatestMoviesMatchesPage(page)
        Mockito.verify(dao).insert(movies)
        Mockito.verifyNoMoreInteractions(dao)
    }
}