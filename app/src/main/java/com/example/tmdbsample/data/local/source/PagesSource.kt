package com.example.tmdbsample.data.local.source

import com.example.tmdbsample.data.local.db.dao.PagesDao
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.data.local.db.model.PageInfoEntity
import com.example.tmdbsample.testing.OpenForTesting

@OpenForTesting
class PagesSource(private val dao: PagesDao) {

    fun getLatestMoviesPage() = getByType(TYPE_LATEST)
    fun setLatestMoviesMatchesPage(page: PageInfo) {
        save(TYPE_LATEST, page)
    }

    private fun save(type: String, page: PageInfo) {
        dao.insert(PageInfoEntity(type, page.total, page.page))
    }

    private fun getByType(type: String) = dao.get(type)

    companion object {
        private const val TYPE_LATEST = "latest"
    }
}
