package com.example.tmdbsample.domain.usecase

import java.util.Calendar
import java.util.Date

class MoviesDateProvider {

    private val DAYS_INTERVAL = 14

    // TODO temporary solution, should be fixed when date will be set up from the UI
    fun getLatestInterval(date: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_INTERVAL)
        val startDate = calendar.time

        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, DAYS_INTERVAL)
        val endDate = calendar.time

        return startDate to endDate
    }
}