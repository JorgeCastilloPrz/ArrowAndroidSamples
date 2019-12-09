package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.FavouritesListView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.asyncListDelegationAdapter
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_favorites_list.*
import kotlinx.android.synthetic.main.item_news_compact.*

class FavoritesListFragment : Fragment(), FavouritesListView {

  private lateinit var adapter: AsyncListDifferDelegationAdapter<NewsItemViewState>

  override fun onAttach(context: Context) {
    super.onAttach(context)

    if (context is AppCompatActivity) setupActivity(context)
  }

  private fun setupActivity(activity: AppCompatActivity) {
    activity.setTitle(R.string.title_favorites_list)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.fragment_favorites_list, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupList()
  }

  private fun setupList() {
    favsList.setHasFixedSize(true)
    favsList.layoutManager = LinearLayoutManager(requireContext())
    adapter = compactNewsAdapter(itemClick = ::onItemClick)
    favsList.adapter = adapter
  }

  private fun onItemClick(newsItemViewState: NewsItemViewState) {
  }

  override fun drawFavs(items: List<NewsItemViewState>) {
    adapter.items = items
  }

  override fun showLoading() {
    loader.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    loader.visibility = View.GONE
  }

  override fun showNotFoundError() {
    Snackbar.make(requireView(), R.string.not_found, Snackbar.LENGTH_SHORT).show()
  }

  override fun showGenericError() {
    Snackbar.make(requireView(), R.string.generic, Snackbar.LENGTH_SHORT).show()
  }

  override fun showAuthenticationError() {
    Snackbar.make(requireView(), R.string.authentication, Snackbar.LENGTH_SHORT).show()
  }
}

fun compactNewsAdapter(itemClick: (NewsItemViewState) -> Unit) =
  asyncListDelegationAdapter(
    { it.hashCode().toLong() },
    compactNewsItemAdapter(itemClick)
  )

fun compactNewsItemAdapter(
  itemClick: (NewsItemViewState) -> Unit
) = adapterDelegateLayoutContainer<NewsItemViewState, NewsItemViewState>(R.layout.item_news_compact) {
  root.setOnClickListener {
    itemClick(item)
  }

  bind {


  }
}
