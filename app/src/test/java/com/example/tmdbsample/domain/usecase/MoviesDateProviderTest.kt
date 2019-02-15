package com.example.tmdbsample.domain.usecase

import com.example.tmdbsample.global.BaseTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class MoviesDateProviderTest : BaseTest() {

    private lateinit var provider: MoviesDateProvider

    @Before
    fun init() {
        provider = MoviesDateProvider()
    }

    @Test
    fun `verify 2 weeks date set up`() {
        val calendar = Calendar.getInstance()

        val date = calendar.apply {
            set(2000, 10, 15)
        }.time

        val (actualStartDate, actualEndDate) = provider.getLatestInterval(date)

        val expectedStartDate = calendar.apply {
            set(2000, 10, 1)
        }.time

        val expectedEndDate = calendar.apply {
            set(2000, 10, 29)
        }.time

        assertThat(actualStartDate, CoreMatchers.`is`(expectedStartDate))
        assertThat(actualEndDate, CoreMatchers.`is`(expectedEndDate))
    }
}