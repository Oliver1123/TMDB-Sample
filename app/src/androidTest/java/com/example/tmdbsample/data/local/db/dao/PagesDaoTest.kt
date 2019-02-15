package com.example.tmdbsample.data.local.db.dao

import com.example.tmdbsample.global.BaseMockTest
import com.example.tmdbsample.utils.TestDataLocal
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.koin.standalone.inject

class PagesDaoTest : BaseMockTest() {
    private val dao: PagesDao by inject()

    private val type = "foo"

    @Test
    fun readEmpty_ReturnNull() {
        val loaded = dao.get(type)

        MatcherAssert.assertThat(loaded, CoreMatchers.nullValue())
    }

    @Test
    fun insertAndReadPageInfo() {
        val page = 0
        val total = 5
        val pageEntity = TestDataLocal.createPageInfoEntity(type, total, page)

        dao.insert(pageEntity)
        val loaded = dao.get(type)

        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded?.page, CoreMatchers.`is`(page))
        MatcherAssert.assertThat(loaded?.total, CoreMatchers.`is`(total))
    }

    @Test
    fun insertMultiplePageInfo_ShouldOverride() {
        val page = 0
        val total = 5
        val ignored = 9

        dao.insert(TestDataLocal.createPageInfoEntity(type, total = ignored, page = ignored))
        dao.insert(TestDataLocal.createPageInfoEntity(type, total = total, page = page))
        val loaded = dao.get(type)

        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded?.page, CoreMatchers.`is`(page))
        MatcherAssert.assertThat(loaded?.total, CoreMatchers.`is`(total))
    }
}
