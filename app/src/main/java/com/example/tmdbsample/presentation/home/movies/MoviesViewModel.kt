package com.example.tmdbsample.presentation.home.movies

import android.arch.lifecycle.LiveData
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.domain.usecase.GetLatestMoviesUseCase
import com.example.tmdbsample.presentation.base.BaseViewModel
import java.util.*

class MoviesViewModel(
    private val getLatestMoviesUseCase: GetLatestMoviesUseCase
): BaseViewModel() {

    private val date = Date()

    val movies: LiveData<Result<List<ShortMovieEntity>>> = getLatestMoviesUseCase(date, true)
}