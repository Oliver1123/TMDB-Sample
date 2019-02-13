package com.example.tmdbsample.data.network.api

import io.reactivex.Single
import retrofit2.http.GET

interface MoviesApi {

    @GET("discover/movie")
    fun getMovies(): Single<Any>
}