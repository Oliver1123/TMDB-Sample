package com.example.tmdbsample.presentation.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutRes(), container, false)
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    fun showDefaultLoader(show: Boolean) {
        baseActivity?.showLoader(show)
    }

    val baseActivity: BaseActivity?
        get() = activity as BaseActivity?

    fun handleError(throwable: Throwable, logMessage: String? = null) {
        baseActivity?.handleError(throwable, logMessage)
    }
}