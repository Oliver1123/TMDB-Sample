package com.example.tmdbsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.tmdbsample.data.network.api.MoviesApi
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val moviesApi: MoviesApi by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO clean up when UI ready
        moviesApi.getMovies()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { t -> Timber.e(t) },
                onSuccess = { data -> Timber.d("getMovies: $data")}
            )
    }
}
