package com.example.tmdbsample.presentation.home.movies.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.tmdbsample.R
import com.example.tmdbsample.utils.inflate

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): LoadingViewHolder {
            val view = parent.inflate(R.layout.view_item_movie_loading)
            return LoadingViewHolder(view)
        }
    }
}