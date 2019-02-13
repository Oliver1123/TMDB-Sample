package com.example.tmdbsample.data.local.db.mapper

import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.network.model.ShortMovieApi

object ShortMovieApiToShortMovieEntityMapper : Mapper<ShortMovieApi, ShortMovieEntity> {
    override fun apply(item: ShortMovieApi): ShortMovieEntity {
        return ShortMovieEntity(
            item.id ?: 0,
            item.title ?: ""
        )
    }
}