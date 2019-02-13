package com.example.tmdbsample.utils

import com.example.tmdbsample.data.network.model.ShortMovieApi

object TestDataApi {

    fun createShortMovieApi(
        id: Long,
        title: String? = null
    ) = ShortMovieApi(id, title)

    fun createShortMovieApiList(
        count: Int,
        id: Long,
        title: String? = null
    ): List<ShortMovieApi> {
        return (0 until count).map {
            createShortMovieApi(
                id + it,
                title + it
            )
        }
    }
}