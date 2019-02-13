package com.example.tmdbsample.presentation.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {
    protected val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}