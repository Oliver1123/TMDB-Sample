package com.example.tmdbsample.presentation.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.tmdbsample.utils.Event
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    private var _errors: MutableLiveData<Event<Throwable>> = MutableLiveData()

    protected val disposable = CompositeDisposable()

    open fun getErrors(): LiveData<Event<Throwable>> = _errors

    fun postError(throwable: Throwable) {
        _errors.postValue(Event(throwable))
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}