package com.example.tmdbsample.data.local.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.data.local.db.model.PageInfoEntity

@Dao
interface PagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pageInfo: PageInfoEntity)

    @Query("SELECT total, page FROM pages WHERE type = :type")
    fun get(type: String): PageInfo?
}