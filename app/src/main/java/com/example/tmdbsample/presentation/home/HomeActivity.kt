package com.example.tmdbsample.presentation.home

import android.os.Bundle
import com.example.tmdbsample.R
import com.example.tmdbsample.presentation.base.BaseActivity
import com.example.tmdbsample.presentation.base.BaseFragment
import com.example.tmdbsample.presentation.home.movies.MoviesFragment

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
}
