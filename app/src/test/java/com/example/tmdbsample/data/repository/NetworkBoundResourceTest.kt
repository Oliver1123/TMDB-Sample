package com.example.tmdbsample.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.global.BaseTest
import com.example.tmdbsample.utils.CountingAppExecutors
import com.example.tmdbsample.utils.InstantAppExecutors
import com.example.tmdbsample.utils.toLiveData
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@RunWith(Parameterized::class)
class NetworkBoundResourceTest(private val useRealExecutors: Boolean) : BaseTest() {

    private lateinit var handleSaveCallResult: (Foo) -> Unit

    private lateinit var handleShouldMatch: (Foo?) -> Boolean

    private lateinit var handleCreateCall: () -> LiveData<Result<Foo>>

    private val dbData = MutableLiveData<Foo>()

    private lateinit var networkBoundResource: NetworkBoundResource<Foo, Foo>

    private val fetchedOnce = AtomicBoolean(false)
    private lateinit var countingAppExecutors: CountingAppExecutors

    init {
        if (useRealExecutors) {
            countingAppExecutors = CountingAppExecutors()
        }
    }

    @Before
    fun init() {
        val appExecutors = if (useRealExecutors)
            countingAppExecutors.appExecutors
        else
            InstantAppExecutors()

        networkBoundResource = object : NetworkBoundResource<Foo, Foo>(appExecutors) {
            override fun saveCallResult(item: Foo) {
                handleSaveCallResult(item)
            }

            override fun shouldFetch(data: Foo?): Boolean {
                // since test methods don't handle repetitive fetching, call it only once
                return handleShouldMatch(data) && fetchedOnce.compareAndSet(false, true)
            }

            override fun loadFromDb(): LiveData<Foo> {
                return dbData
            }

            override fun createCall(): LiveData<Result<Foo>> {
                return handleCreateCall()
            }
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
    fun basicFromNetwork() {
        val saved = AtomicReference<Foo>()
        handleShouldMatch = { it == null }
        val fetchedDbValue = Foo(1)
        handleSaveCallResult = { foo ->
            saved.set(foo)
            dbData.setValue(fetchedDbValue)
        }
        val networkResult = Foo(1)
        handleCreateCall = { singleToLiveData(Single.just(networkResult)) }

        val observer = mock<Observer<Result<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        Mockito.verify(observer).onChanged(Result.loading())
        Mockito.reset(observer)
        dbData.value = null
        drain()
        MatcherAssert.assertThat(saved.get(), CoreMatchers.`is`(networkResult))
        Mockito.verify(observer).onChanged(Result.success(fetchedDbValue))
    }

    @Test
    fun failureFromNetwork() {
        val saved = AtomicBoolean(false)
        handleShouldMatch = { it == null }
        handleSaveCallResult = {
            saved.set(true)
        }
        val error = RuntimeException()
        handleCreateCall = { singleToLiveData(Single.error(error)) }

        val observer = mock<Observer<Result<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        Mockito.verify(observer).onChanged(Result.loading())
        Mockito.reset(observer)
        dbData.value = null
        drain()
        MatcherAssert.assertThat(saved.get(), CoreMatchers.`is`(false))

        Mockito.verify(observer).onChanged(Result.error(error))
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithoutNetwork() {
        val saved = AtomicBoolean(false)
        handleShouldMatch = { it == null }
        handleSaveCallResult = {
            saved.set(true)
        }

        val observer = mock<Observer<Result<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        Mockito.verify(observer).onChanged(Result.loading())
        Mockito.reset(observer)
        val dbFoo = Foo(1)
        dbData.value = dbFoo
        drain()
        Mockito.verify(observer).onChanged(Result.success(dbFoo))
        MatcherAssert.assertThat(saved.get(), CoreMatchers.`is`(false))
        val dbFoo2 = Foo(2)
        dbData.value = dbFoo2
        drain()
        Mockito.verify(observer).onChanged(Result.success(dbFoo2))
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithFetchFailure() {
        val dbValue = Foo(1)
        val saved = AtomicBoolean(false)
        handleShouldMatch = { foo -> foo === dbValue }
        handleSaveCallResult = {
            saved.set(true)
        }

        val error = RuntimeException()
        val apiResponseLiveData = MutableLiveData<Result<Foo>>()
        handleCreateCall = { apiResponseLiveData }

        val observer = mock<Observer<Result<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        Mockito.verify(observer).onChanged(Result.loading())
        Mockito.reset(observer)

        dbData.value = dbValue
        drain()
        Mockito.verify(observer).onChanged(Result.loading(dbValue))

        apiResponseLiveData.value = Result.error(error)
        drain()
        MatcherAssert.assertThat(saved.get(), CoreMatchers.`is`(false))

        Mockito.verify(observer).onChanged(Result.error(error, dbValue))

        val dbValue2 = Foo(2)
        dbData.value = dbValue2
        drain()
        Mockito.verify(observer).onChanged(Result.error(error, dbValue2))
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithReFetchSuccess() {
        val dbValue = Foo(1)
        val dbValue2 = Foo(2)
        val saved = AtomicReference<Foo>()
        handleShouldMatch = { foo -> foo === dbValue }
        handleSaveCallResult = { foo ->
            saved.set(foo)
            dbData.setValue(dbValue2)
        }
        val apiResponseLiveData = MutableLiveData<Result<Foo>>()
        handleCreateCall = { apiResponseLiveData }

        val observer = mock<Observer<Result<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        Mockito.verify(observer).onChanged(Result.loading())
        Mockito.reset(observer)

        dbData.value = dbValue
        drain()
        val networkResult = Foo(1)
        Mockito.verify(observer).onChanged(Result.loading(dbValue))
        apiResponseLiveData.value = Result.success(networkResult)
        drain()
        MatcherAssert.assertThat(saved.get(), CoreMatchers.`is`(networkResult))
        Mockito.verify(observer).onChanged(Result.success(dbValue2))
        Mockito.verifyNoMoreInteractions(observer)
    }

    private data class Foo(var value: Int)

    private fun <T : Any> singleToLiveData(single: Single<T>): LiveData<Result<T>> {
        return single.toLiveData()
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun param(): List<Boolean> {
            return arrayListOf(true, false)
        }
    }
}