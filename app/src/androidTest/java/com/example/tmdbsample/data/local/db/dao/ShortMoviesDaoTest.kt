package com.example.tmdbsample.data.local.db.dao

import com.example.tmdbsample.global.BaseMockTest
import com.example.tmdbsample.utils.LiveDataTestUtil.getValue
import com.example.tmdbsample.utils.TestDataLocal
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.koin.standalone.inject

class ShortMoviesDaoTest : BaseMockTest() {
    private val dao: ShortMoviesDao by inject()

    @Test
    fun insertAndReadDelete() {
        val id1 = 1L
        val id2 = 2L
        val items = listOf(
            TestDataLocal.createShortMovieEntity(id1, "title1"),
            TestDataLocal.createShortMovieEntity(id2, "title2")
        )

        dao.insert(items)
        val loaded = getValue(dao.getMovies())

        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(2))
        MatcherAssert.assertThat(loaded[0].id, CoreMatchers.`is`(id1))
        MatcherAssert.assertThat(loaded[0].title, CoreMatchers.`is`("title1"))

        MatcherAssert.assertThat(loaded[1].id, CoreMatchers.`is`(id2))
        MatcherAssert.assertThat(loaded[1].title, CoreMatchers.`is`("title2"))

        dao.delete(id1)
        val loadedAfterDelete = getValue(dao.getMovies())

        MatcherAssert.assertThat(loadedAfterDelete, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loadedAfterDelete.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(loadedAfterDelete[0].id, CoreMatchers.`is`(id2))
    }

    @Test
    fun insertItemsWithTheSameIdShouldOverride() {
        val items = listOf(
            TestDataLocal.createShortMovieEntity(1L),
            TestDataLocal.createShortMovieEntity(2L)
        )

        dao.insert(items)
        dao.insert(
            listOf(
                TestDataLocal.createShortMovieEntity(1L)
            )
        )
        val loaded = getValue(dao.getMovies())

        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(2))
        MatcherAssert.assertThat(loaded[0].id, CoreMatchers.`is`(1L))
        MatcherAssert.assertThat(loaded[1].id, CoreMatchers.`is`(2L))
    }
}