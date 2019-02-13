package com.example.tmdbsample.global

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.example.tmdbsample.BuildConfig
import com.example.tmdbsample.global.logger.CrashlyticsLogTree
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin
import timber.log.Timber

open class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initCrashlytics()
        initTimber()
        initKoin()
    }

    private fun initCrashlytics() {
        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit)
    }

    public open fun initKoin() {
        startKoin(this, appModules)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogTree())
        }
    }
}