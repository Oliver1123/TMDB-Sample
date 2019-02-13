package com.example.tmdbsample.presentation

import com.example.tmdbsample.presentation.home.movies.MoviesViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { MoviesViewModel(get()) }
}