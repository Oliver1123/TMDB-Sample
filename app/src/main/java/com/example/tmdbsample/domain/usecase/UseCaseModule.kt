package com.example.tmdbsample.domain.usecase

import org.koin.dsl.module.module

val useCaseModule = module {
    single { GetLatestMoviesUseCase(get())}
}