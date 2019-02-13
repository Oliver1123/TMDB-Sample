package com.example.tmdbsample.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.example.tmdbsample.domain.model.Result
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy


fun <T : Any> Single<T>.toLiveData(withLoading: Boolean = false): LiveData<Result<T>> {
    return RxSingleLiveData(this, withLoading = withLoading)
}

fun <T : Any> LiveData<T>.asEvent(): LiveData<Event<T>> {
    return Transformations.map(this) { item ->
        item?.let { Event(item) }
    }
}

fun <T : Any> Single<T>.toLiveData(liveData: MutableLiveData<Result<T>>): Disposable {
    return this.doOnSubscribe { liveData.postValue(Result.loading()) }
        .subscribeBy(
            onSuccess = { result: T ->
                liveData.postValue(Result.success(result))
            },
            onError = { throwable ->
                liveData.postValue(Result.error(throwable))
            }
        )
}

fun <T : Any> Single<T>.toLiveDataEvent(liveData: MutableLiveData<Event<Result<T>>>): Disposable {
    return this.doOnSubscribe { liveData.postValue(Event(Result.loading())) }
        .subscribeBy(
            onSuccess = { result: T ->
                liveData.postValue(Event(Result.success(result)))
            },
            onError = { throwable ->
                liveData.postValue(Event(Result.error(throwable)))
            }
        )
}