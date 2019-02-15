package com.example.tmdbsample.domain.usecase

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import com.example.tmdbsample.domain.model.Result
import java.util.Date

class GetLatestMoviesUseCase(
    private val latestMoviesRepository: LatestMoviesRepository
) {

    operator fun invoke(
        startDate: Date,
        endDate: Date,
        refresh: Boolean
    ): LiveData<Result<List<ShortMovieEntity>>> {
        return latestMoviesRepository.getMovies(startDate, endDate, refresh)
    }
}