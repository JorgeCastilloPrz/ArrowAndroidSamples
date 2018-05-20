package com.github.jorgecastillo.kotlinandroid.tagless.runtime.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import arrow.effects.fix
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.adapter.HeroesCardAdapter
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.model.SuperHeroViewModel
import com.github.jorgecastillo.kotlinandroid.tagless.runtime.instances.instances
import kotlinx.android.synthetic.main.activity_main.*

class SuperHeroListActivity : AppCompatActivity(), SuperHeroesListView {

  private lateinit var adapter: HeroesCardAdapter

  private val pres = instances().presentation()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupList()
  }

  private fun setupList() {
    heroesList.setHasFixedSize(true)
    heroesList.layoutManager = LinearLayoutManager(this)
    adapter = HeroesCardAdapter(itemClick = {
      pres.onHeroListItemClick(this, it.heroId).fix().unsafeRunAsync {}
    })
    heroesList.adapter = adapter
  }

  override fun onResume() {
    super.onResume()
    pres.drawSuperHeroes(this).fix().unsafeRunAsync {}
  }

  override fun drawHeroes(heroes: List<SuperHeroViewModel>) = runOnUiThread {
    adapter.characters = heroes
    adapter.notifyDataSetChanged()
  }

  override fun showNotFoundError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.not_found, Snackbar.LENGTH_SHORT).show()
  }

  override fun showGenericError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.generic, Snackbar.LENGTH_SHORT).show()
  }

  override fun showAuthenticationError() = runOnUiThread {
    Snackbar.make(heroesList, R.string.authentication, Snackbar.LENGTH_SHORT).show()
  }
}

