package com.example.tmdbsample.utils

import org.koin.dsl.module.module
import java.util.concurrent.Executors

val utilsModule = module {
    single { AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), MainThreadExecutor()) }
}