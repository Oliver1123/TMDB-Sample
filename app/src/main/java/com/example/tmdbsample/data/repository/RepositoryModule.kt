package com.example.tmdbsample.data.repository

import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { LatestMoviesRepository(get(), get(), get(), get(), get()) }
}