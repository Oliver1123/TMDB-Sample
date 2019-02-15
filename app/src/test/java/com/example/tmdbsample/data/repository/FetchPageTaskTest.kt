package com.example.tmdbsample.data.repository

import android.arch.lifecycle.Observer
import com.example.tmdbsample.data.local.db.model.PageInfo
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.global.BaseTest
import com.example.tmdbsample.utils.CountingAppExecutors
import com.example.tmdbsample.utils.InstantAppExecutors
import com.example.tmdbsample.utils.TestDataLocal
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import java.io.IOException
import java.util.concurrent.TimeUnit

@RunWith(Parameterized::class)
class FetchPageTaskTest(private val useRealExecutors: Boolean) : BaseTest() {

    private lateinit var countingAppExecutors: CountingAppExecutors

    init {
        if (useRealExecutors) {
            countingAppExecutors = CountingAppExecutors()
        }
    }

    private fun drain() {
        if (!useRealExecutors) {
            return
        }
        try {
            countingAppExecutors.drainTasks(1, TimeUnit.SECONDS)
        } catch (t: Throwable) {
            throw AssertionError(t)
        }
    }

    @Test
    fun `when current page null Return null`() {
        val observer = observeTask(
            mockGetCurrentPage = { null },
            mockGetDataFromApi = { successResult(0) },
            mockHandleSuccessResponse = { false }
        )

        drain()
        Mockito.verify(observer).onChanged(null)
    }

    @Test
    fun `when don't have next page Return success false`() {
        val observer = observeTask(
            mockGetCurrentPage = { TestDataLocal.createPageInfo(1, 0) },
            mockGetDataFromApi = { successResult(0) },
            mockHandleSuccessResponse = { false }
        )

        drain()
        Mockito.verify(observer).onChanged(Result.success(false))
    }

    @Test
    fun `when exception Return error true`() {
        val exception = IOException("tmp")

        val observer = observeTask(
            mockGetCurrentPage = { TestDataLocal.createPageInfo(2, 0) },
            mockGetDataFromApi = { throw exception },
            mockHandleSuccessResponse = { false }
        )

        drain()
        Mockito.verify(observer).onChanged(Result.error(exception, true))
    }

    @Test
    fun `when server empty Return success false`() {

        val observer = observeTask(
            mockGetCurrentPage = { TestDataLocal.createPageInfo(2, 0) },
            mockGetDataFromApi = { Single.just(null) },
            mockHandleSuccessResponse = { false }
        )

        drain()
        Mockito.verify(observer).onChanged(Result.success(false))
    }

    @Test
    fun `when server ok but don't have more Return success false`() {

        val value = 0

        val observer = observeTask(
            mockGetCurrentPage = { TestDataLocal.createPageInfo(2, 0) },
            mockGetDataFromApi = { successResult(value) },
            mockHandleSuccessResponse = { result ->
                assertEquals(Foo(value), result)
                false
            }
        )

        drain()
        Mockito.verify(observer).onChanged(Result.success(false))
    }

    @Test
    fun `when server ok and have more Return success true`() {

        val value = 0

        val observer = observeTask(
            mockGetCurrentPage = { TestDataLocal.createPageInfo(2, 0) },
            mockGetDataFromApi = { successResult(value) },
            mockHandleSuccessResponse = { result ->
                assertEquals(Foo(value), result)
                true
            }
        )

        drain()
        Mockito.verify(observer).onChanged(Result.success(true))
    }

    private fun getExecutors() = if (useRealExecutors)
        countingAppExecutors.appExecutors
    else
        InstantAppExecutors()

    private fun observeTask(
        mockGetCurrentPage: () -> PageInfo?,
        mockGetDataFromApi: (Int) -> Single<Foo>,
        mockHandleSuccessResponse: (response: Foo) -> Boolean
    ): Observer<Result<Boolean>> {

        val task = object : FetchPageTask<Foo>(getExecutors()) {
            override fun getCurrentPage(): PageInfo? = mockGetCurrentPage()

            override fun getDataFromApi(page: Int) = mockGetDataFromApi(page)

            override fun handleSuccessResponse(response: Foo): Boolean =
                mockHandleSuccessResponse(response)
        }

        val observer = mock<Observer<Result<Boolean>>>()
        task.asLiveData().observeForever(observer)
        return observer
    }

    private data class Foo(var value: Int)

    private fun successResult(value: Int): Single<Foo> {
        val response = Foo(value)

        return Single.just(response)
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun param(): List<Boolean> {
            return arrayListOf(true, false)
        }
    }
}