package com.example.tmdbsample.presentation.home.movies.adapter

import android.view.View
import android.view.ViewGroup
import com.example.tmdbsample.R
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.presentation.base.adapater.BaseViewHolder
import com.example.tmdbsample.utils.inflate
import kotlinx.android.synthetic.main.view_item_movie_short.*

class ShortMovieViewHolder(
    view: View,
    private val callback: MoviesAdapter.OnMovieClickListener
) : BaseViewHolder<ShortMovieEntity>(view) {

    init {
        itemView.setOnClickListener {
            callback.onItemClick(this, adapterPosition)
        }
    }

    override fun bind(item: ShortMovieEntity) {
        tv_title.text = item.title
    }

    companion object {
        fun create(
            parent: ViewGroup,
            callback: MoviesAdapter.OnMovieClickListener
        ): ShortMovieViewHolder {
            val view = parent.inflate(R.layout.view_item_movie_short)
            return ShortMovieViewHolder(view, callback)
        }
    }
}