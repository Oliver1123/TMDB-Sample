package com.example.tmdbsample.domain.usecase

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import com.example.tmdbsample.domain.model.Result
import java.util.Date

class LoadNextLatestMoviesPageUseCase(
    private val latestMoviesRepository: LatestMoviesRepository
) {

    operator fun invoke(startDate: Date, endDate: Date): LiveData<Result<Boolean>> {
        return latestMoviesRepository.loadNextPage(startDate, endDate)
    }
}