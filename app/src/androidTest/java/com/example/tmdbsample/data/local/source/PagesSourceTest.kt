package com.example.tmdbsample.data.local.source

import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.global.BaseMockTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.koin.standalone.inject


class PagesSourceTest : BaseMockTest() {
    private val pagesSource: PagesSource by inject()

    @Test
    fun testLatestMoviesPageSaving() {
        val page = 0
        val total = 1

        val before = pagesSource.getLatestMoviesPage()
        assertThat(before, CoreMatchers.nullValue())

        pagesSource.setLatestMoviesMatchesPage(PageInfo.create(page, total))

        val actual = pagesSource.getLatestMoviesPage()

        assertThat(actual?.page, CoreMatchers.`is`(page))
        assertThat(actual?.total, CoreMatchers.`is`(total))
    }
}
