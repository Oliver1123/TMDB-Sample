package com.example.tmdbsample.utils

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.domain.model.Result
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean

class RxSingleLiveData<T>(
    private val single: Single<T>,
    private val worker: Scheduler = Schedulers.io(),
    private val main: Scheduler = AndroidSchedulers.mainThread(),
    private val withLoading: Boolean = false
) : LiveData<Result<T>>() {
    private var started = AtomicBoolean(false)
    private var disposable: Disposable? = null

    override fun onActive() {
        if (started.compareAndSet(false, true)) {
            single.subscribeOn(worker)
                .observeOn(main)
                .doOnSubscribe {
                    if (withLoading) {
                        value = Result.loading()
                    }
                }
                .subscribe(object : SingleObserver<T> {
                    override fun onSuccess(t: T) {
                        value = Result.success(t)
                    }

                    override fun onError(e: Throwable) {
                        value = Result.error(e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }
                })
        }
    }

    override fun onInactive() {
        if (!hasObservers()) {
            disposable?.dispose()
        }
    }
}