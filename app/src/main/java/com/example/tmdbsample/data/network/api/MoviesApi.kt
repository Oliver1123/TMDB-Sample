package com.example.tmdbsample.data.network.api

import com.example.tmdbsample.data.network.model.PagedResult
import com.example.tmdbsample.data.network.model.ShortMovieApi
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("discover/movie")
    fun getMovies(
        @Query("release_date.gte") startDate: String,
        @Query("release_date.lte") endDate: String,
        @Query("page") page: Int = 1
    ): Single<PagedResult<ShortMovieApi>>
}