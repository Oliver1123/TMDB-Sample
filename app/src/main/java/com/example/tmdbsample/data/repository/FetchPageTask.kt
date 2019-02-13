package com.example.tmdbsample.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.utils.AppExecutors
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException

abstract class FetchPageTask<ResponseType>(private val executors: AppExecutors) {
    private val _liveData = MutableLiveData<Result<Boolean>>()

    /**
     * Return request result, Boolean data true if it is possible to request for another page
     */
    fun asLiveData(): LiveData<Result<Boolean>> {
        executors.networkIO().execute {
            run()
        }
        return _liveData
    }

    private fun run() {
        val currentPageInfo = getCurrentPage()
        Timber.d("FetchMatchesPageTask run current: $currentPageInfo ")

        if (currentPageInfo == null) { // we don't have information about any page that was saved
            _liveData.postValue(null)
            return
        }
        if (!currentPageInfo.hasNextPage()) { // we reached the end of the list
            _liveData.postValue(Result.success(false))
            return
        }

        try {
            val response = getDataFromApi(currentPageInfo.page + 1).blockingGet()
            if (response != null) {
                val haveNextPage = handleSuccessResponse(response)
                _liveData.postValue(Result.success(haveNextPage))
            } else { // null not allowed with rx, but still
                _liveData.postValue(Result.success(false))
            }
        } catch (e: NullPointerException) {
            _liveData.postValue(Result.success(false))
        } catch (e: Exception) {
            _liveData.postValue(Result.error(e, true))
        }
    }

    abstract fun getCurrentPage(): PageInfo?

    @Throws(IOException::class)
    abstract fun getDataFromApi(page: Int): Single<ResponseType>

    abstract fun handleSuccessResponse(response: ResponseType): Boolean
}