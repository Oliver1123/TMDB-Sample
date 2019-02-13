package com.example.tmdbsample.data.network.model

import com.google.gson.annotations.SerializedName

data class ShortMovieApi(
    @SerializedName("id") var id: Long?,
    @SerializedName("title") var title: String?
)