package com.example.tmdbsample.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.removeObservers(this)
    liveData.observe(this, Observer(body))
}

fun <T : Any> LifecycleOwner.observeEvent(liveData: LiveData<Event<T>>, body: (T?) -> Unit) {
    liveData.removeObservers(this)
    liveData.observe(this, EventObserver(body))
}

fun ViewGroup.inflate(
    @LayoutRes resource: Int,
    root: ViewGroup? = this,
    attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(this.context).inflate(resource, root, attachToRoot)
}

/**
 * Creates and shows a [Toast] with the given [text]
 *
 * @param duration Toast duration, defaults to [Toast.LENGTH_SHORT]
 */
fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, text, duration).apply { show() }
}

/**
 * Creates and shows a [Toast] with text from a resource
 *
 * @param resId Resource id of the string resource to use
 * @param duration Toast duration, defaults to [Toast.LENGTH_SHORT]
 */
fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, resId, duration).apply { show() }
}

fun RecyclerView.LayoutManager.lastVisiblePosition(): Int {
    return when (this) {
        is LinearLayoutManager -> findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val positions = findLastVisibleItemPositions(null)
            positions.max() ?: RecyclerView.NO_POSITION
        }
        else -> {
            throw IllegalArgumentException("LayoutManager ${this::class} not handled")
        }
    }
}