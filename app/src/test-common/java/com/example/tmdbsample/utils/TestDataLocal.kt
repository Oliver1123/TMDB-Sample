package com.example.tmdbsample.utils

import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.data.local.db.model.PageInfoEntity
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity

object TestDataLocal {
    fun createPageInfo(total: Int, page: Int) = PageInfo(total, page)

    fun createPageInfoEntity(type: String, total: Int, page: Int) =
        PageInfoEntity(type, total, page)

    fun createShortMovieEntity(
        id: Long,
        title: String = ""
    ) = ShortMovieEntity(id, title)

    fun createShortMovieEntityList(
        count: Int,
        id: Long,
        title: String = ""
    ): List<ShortMovieEntity> {
        return (0 until count).map {
            createShortMovieEntity(
                id + it,
                title + it
            )
        }
    }
}