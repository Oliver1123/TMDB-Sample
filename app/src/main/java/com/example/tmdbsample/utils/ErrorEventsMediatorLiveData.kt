package com.example.tmdbsample.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

class ErrorEventsMediatorLiveData<T : Throwable>(vararg values: LiveData<T>) : MediatorLiveData<Event<T>>() {
    init {
        values.forEach {
            addSource(it) { value ->
                // add only if not null and not the same
                if (value != null && this.value?.peekContent()?.message != value.message)
                    this.value = Event(value)
            }
        }
    }
}