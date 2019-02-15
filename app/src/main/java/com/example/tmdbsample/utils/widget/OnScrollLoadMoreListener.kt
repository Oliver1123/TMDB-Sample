package com.example.tmdbsample.utils.widget

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import com.example.tmdbsample.utils.lastVisiblePosition

class OnScrollLoadMoreListener(
    private val offset: Int,
    private val callback: () -> Unit
) : RecyclerView.OnScrollListener() {
    private var lastVisibleItemPosition = RecyclerView.NO_POSITION

    // ignore leak because messages delay is fixed, so leak should not happened
    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            callback.invoke()
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // ignore if was triggered by the system
        if (dx == 0 && dy == 0) return

        val lastVisibleItem =
            recyclerView.layoutManager?.lastVisiblePosition() ?: RecyclerView.NO_POSITION
        if (lastVisibleItem == lastVisibleItemPosition) return
        lastVisibleItemPosition = lastVisibleItem

        if (lastVisibleItem > getItemsCount(recyclerView) - offset) {
            // called to often because if no internet we have a loop:
            // loading view shown (lastVisible changed) -> error -> loading view hidden (last visible changed)
            // so as a solution, trigger callback with delay
            handler.removeMessages(MSG_CALLBACK)
            handler.sendEmptyMessageDelayed(MSG_CALLBACK, CALLBACK_DELAY_MS)
        }
    }

    private fun getItemsCount(recyclerView: RecyclerView): Int =
        recyclerView.adapter?.itemCount ?: 0

    companion object {
        const val CALLBACK_DELAY_MS = 300L
        const val MSG_CALLBACK = 0
    }
}