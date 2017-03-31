package com.github.jorgecastillo.kotlinandroid.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import kotlinx.android.synthetic.main.item_hero.view.*


class HeroesAdapter : RecyclerView.Adapter<HeroesAdapter.ViewHolder>() {

  private val heroes: ArrayList<SuperHeroViewModel> = ArrayList()

  fun renderHeroes(heroes: List<SuperHeroViewModel>) {
    this.heroes.clear()
    this.heroes.addAll(heroes)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hero, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.heroName.text = heroes[position].name
  }

  override fun getItemCount() = heroes.size

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var heroName: TextView = itemView.heroName
  }
}
