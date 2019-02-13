package com.example.tmdbsample.data.local.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.tmdbsample.data.local.db.dao.PagesDao
import com.example.tmdbsample.data.local.db.dao.ShortMoviesDao
import com.example.tmdbsample.data.local.db.model.PageInfoEntity
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity

@Database(
    entities = [PageInfoEntity::class,
        ShortMovieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun moviesDao(): ShortMoviesDao

    abstract fun pagesDao(): PagesDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext, MoviesDatabase::class.java,
                "movies.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
