package com.example.tmdbsample.data.network

import com.example.tmdbsample.data.network.api.MoviesApi
import org.koin.dsl.module.module
import retrofit2.Retrofit

val apiModule = module {

    single { (get() as Retrofit).create<MoviesApi>(MoviesApi::class.java) }
}