package com.example.tmdbsample.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.global.BaseTest
import com.example.tmdbsample.utils.LiveDataTestUtil.getValue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class NextPageHandlerTest : BaseTest() {

    private lateinit var nextPageRequest: () -> LiveData<Result<Boolean>>

    private lateinit var pageHandler: NextPageHandler

    @Before
    fun init() {
        nextPageRequest = mock()
        pageHandler = NextPageHandler(nextPageRequest)
    }

    private val status: LoadMoreState?
        get() = pageHandler.loadMoreState.value

    @Test
    fun constructor() {
        val initial = status
        MatcherAssert.assertThat<LoadMoreState>(initial, CoreMatchers.nullValue())
    }

    @Test
    fun reloadSameValue() {
        val liveData = enqueueNextPageResponse() // send noting
        pageHandler.loadNextPage()
        Mockito.verify(nextPageRequest, Mockito.times(1)).invoke()
        val value = getValue(pageHandler.loadMoreState)
        // request is started but not finished yet
        assertEquals(true, value.isRunning)

        Mockito.reset(nextPageRequest)
        pageHandler.loadNextPage()
        // new request wasn't started because previous not finished
        Mockito.verifyNoMoreInteractions(nextPageRequest)
    }

    @Test
    fun success() {
        val liveData = enqueueNextPageResponse()
        pageHandler.loadNextPage()
        Mockito.verify(nextPageRequest).invoke()
        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(true))

        // start loading
        liveData.postValue(Result.loading())

        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(true))
        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(true))

        // loading finished, can be loaded another page
        liveData.postValue(Result.success(true))

        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(false))
        MatcherAssert.assertThat(pageHandler.hasMore, CoreMatchers.`is`(true))
        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(false))

        // requery
        Mockito.reset(nextPageRequest)
        val nextPageLiveData = enqueueNextPageResponse()
        pageHandler.loadNextPage()
        Mockito.verify(nextPageRequest).invoke()
        MatcherAssert.assertThat(nextPageLiveData.hasActiveObservers(), CoreMatchers.`is`(true))

        // loading finished, don't have another page to load
        nextPageLiveData.postValue(Result.success(false))

        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(false))
        MatcherAssert.assertThat(pageHandler.hasMore, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(nextPageLiveData.hasActiveObservers(), CoreMatchers.`is`(false))

        // retry, no query
        Mockito.reset(nextPageRequest)
        pageHandler.loadNextPage()
        Mockito.verifyNoMoreInteractions(nextPageRequest)
        pageHandler.loadNextPage()
        Mockito.verifyNoMoreInteractions(nextPageRequest)
    }

    @Test
    fun failure() {
        val liveData = enqueueNextPageResponse()
        pageHandler.loadNextPage()
        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(true))

        // error happened, but flag to try again
        val error = Throwable("failed")
        liveData.postValue(Result.error(error, true))

        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(false))
        MatcherAssert.assertThat(status?.error, CoreMatchers.`is`(error))
        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(pageHandler.hasMore, CoreMatchers.`is`(true))

        Mockito.reset(nextPageRequest)
        val liveData2 = enqueueNextPageResponse()
        pageHandler.loadNextPage()
        MatcherAssert.assertThat(liveData2.hasActiveObservers(), CoreMatchers.`is`(true))
        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(true))

        // loading finished, don't have another page to load
        liveData2.postValue(Result.success(false))

        MatcherAssert.assertThat(status?.isRunning, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(status?.error, CoreMatchers.`is`(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(pageHandler.hasMore, CoreMatchers.`is`(false))
    }

    @Test
    fun nullOnChanged() {
        val liveData = enqueueNextPageResponse()
        pageHandler.loadNextPage()
        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(true))
        liveData.postValue(null)
        MatcherAssert.assertThat(liveData.hasActiveObservers(), CoreMatchers.`is`(false))
    }

    private fun enqueueNextPageResponse(): MutableLiveData<Result<Boolean>> {
        val liveData = MutableLiveData<Result<Boolean>>()
        whenever(nextPageRequest.invoke()).thenReturn(liveData)
        return liveData
    }
}