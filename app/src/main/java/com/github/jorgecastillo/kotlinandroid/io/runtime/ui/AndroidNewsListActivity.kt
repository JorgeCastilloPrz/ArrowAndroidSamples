package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.fx.IO
import arrow.fx.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.NewsListView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.NewsRecyclerAdapter
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.getAllNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.onNewsItemClick
import com.github.jorgecastillo.kotlinandroid.io.runtime.application
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.runtime
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class AndroidNewsListActivity : AppCompatActivity(), NewsListView {

    private lateinit var adapter: NewsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupList()
    }

    private fun setupList() {
        newsList.setHasFixedSize(true)
        newsList.layoutManager = LinearLayoutManager(this)
        adapter = NewsRecyclerAdapter(itemClick = onNewsItemClick())
        newsList.adapter = adapter
    }

    private fun onNewsItemClick() = { newsItemViewState: NewsItemViewState ->
        unsafe {
            runNonBlocking({
                IO.runtime(application().runtimeContext).onNewsItemClick(
                        this@AndroidNewsListActivity,
                        newsItemViewState.title)
            }, {})
        }
    }

    override fun onResume() {
        super.onResume()
        unsafe {
            runNonBlocking({
                IO.runtime(application().runtimeContext).getAllNews(this@AndroidNewsListActivity)
            }, {})
        }
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawNews(news: List<NewsItemViewState>) {
        adapter.news = news
        adapter.notifyDataSetChanged()
    }

    override fun showNotFoundError() {
        Snackbar.make(newsList, R.string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() {
        Snackbar.make(newsList, R.string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() {
        Snackbar.make(newsList, R.string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}

