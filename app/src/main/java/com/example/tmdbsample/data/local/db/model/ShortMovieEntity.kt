package com.example.tmdbsample.data.local.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// parcelable needed to be able to save/restore data on the UI
@Parcelize
@Entity(tableName = "short_movies")
data class ShortMovieEntity(
    @PrimaryKey @ColumnInfo(name = "id") var id: Long,
    @ColumnInfo(name = "title") var title: String = ""
) : Parcelable