package com.example.tmdbsample.data.local.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity

@Dao
abstract class ShortMoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(items: List<ShortMovieEntity>)

    @Query("SELECT * FROM short_movies")
    abstract fun getMovies(): LiveData<List<ShortMovieEntity>>

    @Query("DELETE FROM short_movies WHERE id = :id")
    abstract fun delete(id: Long): Int

    @Query("DELETE FROM short_movies")
    abstract fun clear()
}