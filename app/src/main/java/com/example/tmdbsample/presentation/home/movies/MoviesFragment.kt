package com.example.tmdbsample.presentation.home.movies


import android.os.Bundle
import com.example.tmdbsample.R
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.presentation.base.BaseFragment
import com.example.tmdbsample.utils.observe
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MoviesFragment : BaseFragment() {

    private val moviesViewModel: MoviesViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_movies

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observe(moviesViewModel.movies) { result ->
            Timber.d("moviesViewModel.movies: $result")
            handleMovies(result)
        }
    }

    private fun handleMovies(result: Result<List<ShortMovieEntity>>?) {

    }

    companion object {
        fun newInstance(): MoviesFragment = MoviesFragment()
    }
}
