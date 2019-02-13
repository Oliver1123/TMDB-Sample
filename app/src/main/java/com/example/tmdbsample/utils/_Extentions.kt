package com.example.tmdbsample.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer


fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.removeObservers(this)
    liveData.observe(this, Observer(body))
}

fun <T : Any> LifecycleOwner.observeEvent(liveData: LiveData<Event<T>>, body: (T?) -> Unit) {
    liveData.removeObservers(this)
    liveData.observe(this, EventObserver(body))
}