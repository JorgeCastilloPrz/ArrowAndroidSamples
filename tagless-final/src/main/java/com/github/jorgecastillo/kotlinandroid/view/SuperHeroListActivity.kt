package com.github.jorgecastillo.kotlinandroid.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.ev
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.github.jorgecastillo.kotlinandroid.presentation.getSuperHeroes
import com.github.jorgecastillo.kotlinandroid.view.adapter.HeroesCardAdapter
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import kotlinx.android.synthetic.main.activity_main.*

class SuperHeroListActivity : AppCompatActivity(), SuperHeroesView {

  private lateinit var adapter: HeroesCardAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupList()
  }

  fun setupList() {
    heroesList.setHasFixedSize(true)
    heroesList.layoutManager = LinearLayoutManager(this)
    adapter = HeroesCardAdapter(itemClick = {})
    heroesList.adapter = adapter
  }

  override fun onResume() {
    super.onResume()
    getSuperHeroes(AsyncResult).ev().run(GetHeroesContext(this))
  }

  override fun drawHeroes(heroes: List<SuperHeroViewModel>) = runOnUiThread {
    adapter.characters = heroes
    adapter.notifyDataSetChanged()
  }

  override fun showHeroesNotFoundError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.not_found, Snackbar.LENGTH_SHORT).show()
  }

  override fun showGenericError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.generic, Snackbar.LENGTH_SHORT).show()
  }

  override fun showAuthenticationError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.authentication, Snackbar.LENGTH_SHORT).show()
  }
}
