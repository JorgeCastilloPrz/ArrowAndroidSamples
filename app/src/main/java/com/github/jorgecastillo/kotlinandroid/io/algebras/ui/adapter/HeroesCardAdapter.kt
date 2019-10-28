package com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.HeroesCardAdapter.ViewHolder
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.HeroViewState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_hero_card.view.*

class HeroesCardAdapter(
        var characters: List<HeroViewState> = ArrayList(),
        val itemClick: (HeroViewState) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hero_card, parent, false)
    return ViewHolder(view, itemClick)
  }

  override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
    holder.bind(characters[pos])
  }

  override fun getItemCount() = characters.size

  class ViewHolder(view: View,
      val itemClick: (HeroViewState) -> Unit) : RecyclerView.ViewHolder(
      view) {

    fun bind(hero: HeroViewState) {
      with(hero) {
        Picasso.get().load(photoUrl).into(itemView.picture)
        itemView.setOnClickListener { itemClick(this) }
      }
    }
  }
}
