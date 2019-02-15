package com.example.tmdbsample.presentation.home.movies

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.repository.LoadMoreState
import com.example.tmdbsample.data.repository.NextPageHandler
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.domain.usecase.GetLatestMoviesUseCase
import com.example.tmdbsample.domain.usecase.LoadNextLatestMoviesPageUseCase
import com.example.tmdbsample.domain.usecase.MoviesDateProvider
import com.example.tmdbsample.presentation.base.BaseViewModel
import com.example.tmdbsample.utils.AbsentLiveData
import com.example.tmdbsample.utils.ErrorEventsMediatorLiveData
import com.example.tmdbsample.utils.Event
import timber.log.Timber
import java.util.Date

class MoviesViewModel(
    private val getLatestMoviesUseCase: GetLatestMoviesUseCase,
    private val loadNextLatestMoviesPageUseCase: LoadNextLatestMoviesPageUseCase,
    private val moviesDateProvider: MoviesDateProvider
) : BaseViewModel() {

    private val date: Pair<Date, Date> = moviesDateProvider.getLatestInterval(Date())

    private val refreshTrigger = MutableLiveData<Boolean>()

    private val nextPageHandler = NextPageHandler {
        loadNextLatestMoviesPageUseCase(date.first, date.second)
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    val results: LiveData<Result<List<ShortMovieEntity>>> = Transformations
        .switchMap(refreshTrigger) { refresh ->
            if (refresh == null) {
                AbsentLiveData.create()
            } else {
                getLatestMoviesUseCase(date.first, date.second, refresh)
            }
        }

    private val visitorsErrors: LiveData<Throwable> = Transformations.map(results) { it?.error }
    private val loadMoreErrors: LiveData<Throwable> = Transformations.map(loadMoreStatus) { it?.error }

    private val combined = ErrorEventsMediatorLiveData(visitorsErrors, loadMoreErrors)

    init {
        refreshTrigger.postValue(true)
    }

    override fun getErrors(): LiveData<Event<Throwable>> {
        return combined
    }

    fun refresh() {
        Timber.d("refresh: ")
        refreshTrigger.postValue(true)
        nextPageHandler.reset()
        combined.postValue(null)
    }

    fun loadNextPage() {
        Timber.d("loadNextPage: ")
        nextPageHandler.loadNextPage()
    }
}