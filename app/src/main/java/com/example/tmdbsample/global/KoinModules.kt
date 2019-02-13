package com.example.tmdbsample.global

import com.example.tmdbsample.data.di.apiModule
import com.example.tmdbsample.data.di.localModule
import com.example.tmdbsample.data.di.networkModule
import com.example.tmdbsample.data.repository.repositoryModule
import com.example.tmdbsample.domain.usecase.useCaseModule
import com.example.tmdbsample.presentation.viewModelModule
import com.example.tmdbsample.utils.utilsModule


val appModules = listOf(
    networkModule,
    apiModule,
    localModule,
    repositoryModule,

    useCaseModule,
    viewModelModule,

    utilsModule
)