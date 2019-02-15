/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tmdbsample.presentation.base.adapater

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import java.util.concurrent.Executor

/**
 * A generic RecyclerView adapter that uses & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
</V></T> */
abstract class DifferListAdapter<T>(
    executor: Executor,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, RecyclerView.ViewHolder>(
        AsyncDifferConfig.Builder<T>(diffCallback)
                .setBackgroundThreadExecutor(executor)
                .build()
) {

    private var loadNextPageCallback: () -> Unit = {}

    private var nextPageOffset: Int = 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        checkToLoadMore(position)
    }

    private fun checkToLoadMore(position: Int) {
        if (position == itemCount - nextPageOffset) {
            loadNextPageCallback.invoke()
        }
    }

    fun setNextPageCallback(offset: Int, callback: () -> Unit) {
        nextPageOffset = offset
        loadNextPageCallback = callback
    }

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }
}
