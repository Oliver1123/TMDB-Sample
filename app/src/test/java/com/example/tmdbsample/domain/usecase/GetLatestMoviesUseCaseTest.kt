package com.example.tmdbsample.domain.usecase

import com.example.tmdbsample.data.repository.movies.LatestMoviesRepository
import com.example.tmdbsample.global.BaseTest
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.Calendar

class GetLatestMoviesUseCaseTest : BaseTest() {

    private val repository = mock<LatestMoviesRepository>()

    private lateinit var useCase: GetLatestMoviesUseCase

    @Before
    fun init() {
        useCase = GetLatestMoviesUseCase(repository)
    }

    @Test
    fun `verify 2 weeks date set up`() {
        val calendar = Calendar.getInstance()

        val date = calendar.apply {
            set(2000, 10, 15)
        }.time

        useCase(date, true)

        val expectedStartDate = calendar.apply {
            set(2000, 10, 1)
        }.time

        val expectedEndDate = calendar.apply {
            set(2000, 10, 29)
        }.time

        Mockito.verify(repository).getMovies(expectedStartDate, expectedEndDate, true)
        Mockito.verifyNoMoreInteractions(repository)
    }
}