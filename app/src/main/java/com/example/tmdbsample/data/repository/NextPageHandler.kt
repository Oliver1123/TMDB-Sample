package com.example.tmdbsample.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.domain.model.Status

data class LoadMoreState(
    val isRunning: Boolean = false,
    val error: Throwable? = null,
    val hasMore: Boolean
)

/**
 * Next page handler that notify about next page request success/failure status and know if we have next page
 * @param nextPageRequest request for next page, Boolean data - true if can be loaded more
 */
class NextPageHandler(private val nextPageRequest: () -> LiveData<Result<Boolean>>) : Observer<Result<Boolean>> {
    private var nextPageLiveData: LiveData<Result<Boolean>>? = null
    val loadMoreState = MutableLiveData<LoadMoreState>()
    private var _hasMore: Boolean = false
    val hasMore
        get() = _hasMore

    init {
        reset()
    }

    fun loadNextPage() {
        if (loadMoreState.value?.isRunning == true) {
            return
        }
        if (!hasMore) {
            return
        }
        unregister()

        nextPageLiveData = nextPageRequest.invoke()
        loadMoreState.postValue(LoadMoreState(isRunning = true, hasMore = hasMore))
        nextPageLiveData?.observeForever(this)
    }

    override fun onChanged(result: Result<Boolean>?) {
        if (result == null) {
            reset()
        } else {
            when (result.status) {
                Status.SUCCESS -> {
                    _hasMore = result.data == true
                    loadMoreState.postValue(LoadMoreState(isRunning = false, hasMore = hasMore))
                    unregister()
                }
                Status.ERROR -> {
                    _hasMore = true
                    loadMoreState.postValue(LoadMoreState(isRunning = false, error = result.error, hasMore = hasMore))
                    unregister()
                }
                Status.LOADING -> {
                    // ignore
                }
            }
        }
    }

    private fun unregister() {
        nextPageLiveData?.removeObserver(this)
        nextPageLiveData = null
    }

    fun reset() {
        unregister()
        _hasMore = true
        loadMoreState.postValue(null)
    }
}