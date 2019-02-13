package com.example.tmdbsample.data.di

import com.example.tmdbsample.data.local.db.MoviesDatabase
import com.example.tmdbsample.data.local.source.PagesSource
import org.koin.dsl.module.module

val localModule = module {

    single { MoviesDatabase.buildDatabase(get()) }
    single { (get() as MoviesDatabase).moviesDao() }

    single { (get() as MoviesDatabase).pagesDao() }

    single { PagesSource(get()) }
}