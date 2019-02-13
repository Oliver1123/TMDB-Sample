package com.example.tmdbsample.data.local.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "pages")
data class PageInfoEntity(
    @PrimaryKey @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "total") var total: Int,
    @ColumnInfo(name = "page") var page: Int
)

data class PageInfo(
    var total: Int,
    var page: Int
) {
    fun hasNextPage(): Boolean {
        return page < total
    }

    companion object {
        fun create(page: Int?, total: Int?) = PageInfo(total = total ?: 0, page = page ?: 0)
    }
}