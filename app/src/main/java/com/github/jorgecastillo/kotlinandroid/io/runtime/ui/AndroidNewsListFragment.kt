package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_news_list.*

class AndroidNewsListFragment : Fragment(), NewsListView {

    private lateinit var adapter: NewsRecyclerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) context.setTitle(R.string.title_news_list)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_news_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
    }

    private fun setupList() {
        newsList.setHasFixedSize(true)
        newsList.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsRecyclerAdapter(itemClick = ::onNewsItemClick)
        newsList.adapter = adapter
    }

    private fun onNewsItemClick(newsItemViewState: NewsItemViewState): Unit {
        unsafe {
            runNonBlocking({
                IO.runtime(application().runtimeContext).onNewsItemClick(
                        requireContext(),
                        newsItemViewState.title)
            }, {})
        }
    }

    override fun onResume() {
        super.onResume()
        unsafe {
            runNonBlocking({
                IO.runtime(application().runtimeContext).getAllNews(this@AndroidNewsListFragment)
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

