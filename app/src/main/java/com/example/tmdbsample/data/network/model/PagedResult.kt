package com.example.tmdbsample.data.network.model

import com.google.gson.annotations.SerializedName

data class PagedResult<T>(
    @SerializedName("page") var page: Int?,
    @SerializedName("total_pages") var total: Int?,
    @SerializedName("results") var results: List<T>?

)