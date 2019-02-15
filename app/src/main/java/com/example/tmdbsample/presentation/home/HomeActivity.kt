package com.example.tmdbsample.presentation.home

import android.os.Bundle
import android.view.View
import com.example.tmdbsample.R
import com.example.tmdbsample.presentation.base.BaseActivity
import com.example.tmdbsample.presentation.base.BaseFragment
import com.example.tmdbsample.presentation.home.movies.MoviesFragment
import kotlinx.android.synthetic.main.layout_loader.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setFragment(MoviesFragment.newInstance())
    }

    private fun setFragment(fragment: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment, fragment.javaClass.simpleName)
        transaction.commit()
    }

    override fun showLoader(show: Boolean) {
        loader_container.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }
}
