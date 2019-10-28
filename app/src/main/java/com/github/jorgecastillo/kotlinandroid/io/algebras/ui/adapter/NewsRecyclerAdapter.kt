package com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.NewsRecyclerAdapter.ViewHolder
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news.view.*

class NewsRecyclerAdapter(
    var news: List<NewsItemViewState> = ArrayList(),
    val itemClick: (NewsItemViewState) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(news[pos])
    }

    override fun getItemCount() = news.size

    class ViewHolder(
        view: View,
        val itemClick: (NewsItemViewState) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(newsItem: NewsItemViewState) {
            with(newsItem) {
                Picasso.get().load(photoUrl).into(itemView.picture)
                itemView.title.text = newsItem.title
                itemView.description.text = newsItem.description
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}
