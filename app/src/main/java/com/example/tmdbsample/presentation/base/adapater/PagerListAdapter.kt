package com.example.tmdbsample.presentation.base.adapater

import android.support.v7.util.DiffUtil
import java.util.concurrent.Executor

abstract class PagerListAdapter<T>(executor: Executor, diffCallback: DiffUtil.ItemCallback<T>) :
    DifferListAdapter<T>(executor, diffCallback) {
    private var hasMore: Boolean = false

    abstract fun getLoadingItemsCount(): Int

    public fun setHasMoreItems(hasMore: Boolean) {
        if (this.hasMore == hasMore) return

        this.hasMore = hasMore
        val currentCount = itemCount
        if (hasMore) {
            notifyItemRangeInserted(currentCount - getLoadingItemsCount(), getLoadingItemsCount())
        } else {
            notifyItemRangeRemoved(currentCount, getLoadingItemsCount())
        }
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        return itemCount + getLoadingItemsCount(hasMore)
    }

    private fun getLoadingItemsCount(hasMore: Boolean) = if (hasMore) getLoadingItemsCount() else 0

    public fun isDataItem(position: Int): Boolean {
        return position < itemCount - getLoadingItemsCount(hasMore)
    }
}