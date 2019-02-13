package com.example.tmdbsample.data.di

import com.example.tmdbsample.BuildConfig
import com.example.tmdbsample.data.network.interceptor.ServerAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "BASE_URL"
private const val TIMEOUT_SEC: Long = 30

val networkModule = module {
    single(BASE_URL) { getBaseUrl() }

    single { ServerAuthInterceptor() }

    single { getClient(get()) }
    single {
        getRetrofit(
            get(),
            get(BASE_URL)
        )
    }
}


private fun getBaseUrl(): String =
    "${BuildConfig.URL_BASE}/${BuildConfig.API_VERSION}/"

fun getClient(authInterceptor: ServerAuthInterceptor): OkHttpClient {

    val builder = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
        .addNetworkInterceptor(getLoggingInterceptor())
        .addInterceptor(authInterceptor)

    return builder.build()
}

fun getRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)

        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}

fun getLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()

    // Can be Level.BASIC, Level.HEADERS, or Level.BODY
    httpLoggingInterceptor.level = if (BuildConfig.DEBUG)
//            HttpLoggingInterceptor.Level.BASIC
        HttpLoggingInterceptor.Level.BODY
    else
        HttpLoggingInterceptor.Level.NONE

    return httpLoggingInterceptor
}
