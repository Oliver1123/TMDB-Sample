package com.example.tmdbsample.presentation.home.movies.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.presentation.base.adapater.PagerListAdapter
import java.util.concurrent.Executor

class MoviesAdapter(executor: Executor, val callback: OnMovieClickListener) :
    PagerListAdapter<ShortMovieEntity>(executor, COMPARATOR) {

    private val LOADING_ITEMS_COUNT = 1

    private val TYPE_NORMAL = 0
    private val TYPE_LOADER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NORMAL -> ShortMovieViewHolder.create(parent, callback)
            TYPE_LOADER -> LoadingViewHolder.create(parent)
            else -> throw IllegalArgumentException("Unknown viewType '$viewType'")
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (!isDataItem(position)) return TYPE_LOADER

        return TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (isDataItem(position)) {
            val item = getItem(position)
            (holder as ShortMovieViewHolder).bind(item)
        }
    }

    override fun getLoadingItemsCount() = LOADING_ITEMS_COUNT

    interface OnMovieClickListener {
        fun onItemClick(holder: ShortMovieViewHolder, position: Int)
    }

    companion object {

        private val COMPARATOR = object : DiffUtil.ItemCallback<ShortMovieEntity>() {
            override fun areItemsTheSame(
                oldItem: ShortMovieEntity,
                newItem: ShortMovieEntity
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ShortMovieEntity,
                newItem: ShortMovieEntity
            ): Boolean =
                oldItem == newItem
        }
    }
}