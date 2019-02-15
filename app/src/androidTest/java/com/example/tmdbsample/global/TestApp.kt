package com.example.tmdbsample.global

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.example.tmdbsample.data.local.db.MoviesDatabase
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import timber.log.Timber

class TestApp : App() {

    override fun initKoin() {
        Timber.d("initKoin: override dependencies that required for tests")
        val modules = mutableListOf<Module>()
        modules.addAll(appModules)
        modules.add(module {
            single(override = true) {
                Room.inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getContext(),
                    MoviesDatabase::class.java
                ).build()
            }
        })

        startKoin(this, modules)
    }
}