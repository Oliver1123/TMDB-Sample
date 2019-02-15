package com.example.tmdbsample.presentation.base

import android.support.v7.app.AppCompatActivity
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    abstract fun showLoader(show: Boolean)

    fun handleError(throwable: Throwable, logMessage: String? = null) {
        Timber.e(RuntimeException(logMessage, throwable))
    }
}