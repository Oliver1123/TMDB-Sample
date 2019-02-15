package com.example.tmdbsample.global

import android.support.test.runner.AndroidJUnit4
import com.example.tmdbsample.data.local.db.MoviesDatabase
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@RunWith(AndroidJUnit4::class)
abstract class BaseMockTest : KoinComponent {

    val db: MoviesDatabase by inject()

    @Before
    fun initTest() {
        db.clearAllTables()
    }
}