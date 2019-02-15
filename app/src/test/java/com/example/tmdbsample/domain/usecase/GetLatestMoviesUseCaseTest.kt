package com.example.tmdbsample.domain.usecase

import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import com.example.tmdbsample.global.BaseTest
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.Date

class GetLatestMoviesUseCaseTest : BaseTest() {

    private val repository = mock<LatestMoviesRepository>()

    private lateinit var useCase: GetLatestMoviesUseCase

    @Before
    fun init() {
        useCase = GetLatestMoviesUseCase(repository)
    }

    @Test
    fun `verify that repository method is triggered`() {
        val date = Date()

        useCase(date, date, true)

        Mockito.verify(repository).getMovies(date, date, true)
        Mockito.verifyNoMoreInteractions(repository)
    }
}