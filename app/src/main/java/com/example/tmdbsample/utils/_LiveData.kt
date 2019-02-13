package com.example.tmdbsample.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations

fun <T> LiveData<T>.asEvent(): LiveData<Event<T>> = Transformations.map(this) { Event(it) }