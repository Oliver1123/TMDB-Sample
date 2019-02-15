package com.example.tmdbsample.presentation.home.movies

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.tmdbsample.R
import com.example.tmdbsample.data.local.db.model.ShortMovieEntity
import com.example.tmdbsample.domain.model.Result
import com.example.tmdbsample.domain.model.isLoading
import com.example.tmdbsample.presentation.base.BaseFragment
import com.example.tmdbsample.presentation.home.movies.adapter.MoviesAdapter
import com.example.tmdbsample.presentation.home.movies.adapter.ShortMovieViewHolder
import com.example.tmdbsample.utils.AppExecutors
import com.example.tmdbsample.utils.observe
import com.example.tmdbsample.utils.observeEvent
import com.example.tmdbsample.utils.toast
import com.example.tmdbsample.utils.widget.OnScrollLoadMoreListener
import kotlinx.android.synthetic.main.fragment_movies.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MoviesFragment : BaseFragment() {

    private val moviesViewModel: MoviesViewModel by viewModel()
    private val appExecutors: AppExecutors by inject()

    private val movieClickListener: MoviesAdapter.OnMovieClickListener =
        object : MoviesAdapter.OnMovieClickListener {
            override fun onItemClick(holder: ShortMovieViewHolder, position: Int) {
                if (position != RecyclerView.NO_POSITION) {
                    val item = adapter.getItem(position)
                    handleMovieClick(holder, item)
                }
            }
        }

    private lateinit var adapter: MoviesAdapter

    override fun getLayoutRes() = R.layout.fragment_movies

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        swipe_container.setOnRefreshListener {
            onRefreshTriggered()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observe(moviesViewModel.results) { result ->
            Timber.d("moviesViewModel.movies: $result")
            handleMovies(result)
        }

        observe(moviesViewModel.loadMoreStatus) { loadMoreState ->
            Timber.d("loadMore changed: $loadMoreState")
            val hasMore = loadMoreState?.hasMore == true
            val noErrors = loadMoreState?.error == null
            setHasMoreItems(hasMore && noErrors)
            // ignore error here, will be handled by getErrors()
        }

        observeEvent(moviesViewModel.getErrors()) { error ->
            Timber.d("moviesViewModel: error: $error")
            error?.let { handleError(it, "Error while loading movies")
            }
        }
    }

    private fun initRecyclerView() {
        adapter = MoviesAdapter(appExecutors.diskIO(), movieClickListener)
        movies_list.adapter = adapter

        movies_list.layoutManager = LinearLayoutManager(context)
        movies_list.addOnScrollListener(OnScrollLoadMoreListener(MOVIES_OFFSET) {
            onLoadNextPage()
        })

        movies_list.itemAnimator = DefaultItemAnimator()
    }

    private fun handleMovies(result: Result<List<ShortMovieEntity>>?) {
        val haveData = result?.data?.isNotEmpty() == true

        showLoader(result.isLoading(), haveData)
        if (!result.isLoading()) {
            showEmptyList(!haveData)
        }
        if (haveData) {
            setData(result?.data)
        }
    }

    fun setHasMoreItems(hasMore: Boolean) {
        adapter.setHasMoreItems(hasMore)
    }

    fun setData(items: List<ShortMovieEntity>?) {
        adapter.submitList(items)
    }

    private fun showLoader(loading: Boolean, haveData: Boolean) {
        val refreshing = swipe_container.isRefreshing
        // show loader only if !haveData && !refreshing
        // if !loading -> hide loader and finish refreshing
        if (loading) {
            val show = !refreshing && !haveData
            showDefaultLoader(show)
        } else {
            showDefaultLoader(false)
            swipe_container.isRefreshing = false
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            movies_list.visibility = View.GONE
            no_data.visibility = View.VISIBLE
        } else {
            movies_list.visibility = View.VISIBLE
            no_data.visibility = View.GONE
        }
    }

    private fun onRefreshTriggered() {
        moviesViewModel.refresh()
    }

    private fun onLoadNextPage() {
        moviesViewModel.loadNextPage()
    }

    private fun handleMovieClick(holder: ShortMovieViewHolder, item: ShortMovieEntity) {
        activity?.toast("Click on movie '${item.title}'")
    }

    companion object {

        private const val MOVIES_OFFSET = 5

        fun newInstance(): MoviesFragment = MoviesFragment()
    }
}
