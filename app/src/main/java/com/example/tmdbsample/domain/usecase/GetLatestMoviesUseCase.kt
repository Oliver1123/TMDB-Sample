package com.example.tmdbsample.domain.usecase

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import com.example.tmdbsample.domain.model.Result
import java.util.Calendar
import java.util.Date

class GetLatestMoviesUseCase(private val latestMoviesRepository: LatestMoviesRepository) {

    private val DAYS_INTERVAL = 14

    operator fun invoke(now: Date, refresh: Boolean): LiveData<Result<List<ShortMovieEntity>>> {
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_INTERVAL)
        val startDate = calendar.time

        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, DAYS_INTERVAL)
        val endDate = calendar.time

        return latestMoviesRepository.getMovies(startDate, endDate, refresh)
    }
}