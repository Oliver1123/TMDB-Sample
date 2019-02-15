package com.example.tmdbsample.presentation.base.adapater

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<T>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    abstract fun bind(item: T)
}