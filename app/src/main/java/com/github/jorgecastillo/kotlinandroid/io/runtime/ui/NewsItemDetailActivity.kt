package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import arrow.fx.IO
import arrow.fx.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.R.string
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.NewsItemDetailView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.extensions.loadImageAsync
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.getNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.application
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.runtime
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*

class NewsItemDetailActivity : AppCompatActivity(), NewsItemDetailView {

    companion object {
        const val EXTRA_NEWS_ID = "EXTRA_ID"

        fun launch(
            source: Context,
            newsId: String
        ) {
            val intent = Intent(source, NewsItemDetailActivity::class.java)
            intent.putExtra(EXTRA_NEWS_ID, newsId)
            source.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }

    override fun onResume() {
        super.onResume()
        intent.extras?.let {
            val newsId = it.getString(EXTRA_NEWS_ID)
            if (newsId == null) {
                closeWithError()
            } else {
                loadNewsItemDetails(newsId)
            }
        } ?: closeWithError()
    }

    private fun loadNewsItemDetails(title: String) {
        unsafe {
            runNonBlocking({
                IO.runtime(application().runtimeContext).getNewsItemDetails(
                    title,
                    this@NewsItemDetailActivity)
            }, {})
        }
    }

    private fun closeWithError() {
        Toast.makeText(this, string.news_id_needed, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawNewsItem(newsItem: NewsItemViewState) {
        collapsingToolbar.title = newsItem.title
        description.text = newsItem.description ?: getString(string.empty_description)
        newsItem.photoUrl?.let { url -> headerImage.loadImageAsync(url) }
    }

    override fun showNotFoundError() {
        Snackbar.make(appBar, string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() {
        Snackbar.make(appBar, string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() {
        Snackbar.make(appBar, string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}
