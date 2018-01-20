package com.github.jorgecastillo.kotlinandroid.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import arrow.effects.IO
import arrow.effects.asyncContext
import arrow.effects.ev
import arrow.effects.monadError
import arrow.free.foldMap
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.free.algebra.FreeHeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.interpreter.interpreter
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.presentation.showSuperHeroes
import com.github.jorgecastillo.kotlinandroid.view.adapter.HeroesCardAdapter
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import kotlinx.android.synthetic.main.activity_main.*

class SuperHeroListActivity : AppCompatActivity(), SuperHeroesListView {

  private lateinit var adapter: HeroesCardAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupList()
  }

  private fun setupList() {
    heroesList.setHasFixedSize(true)
    heroesList.layoutManager = LinearLayoutManager(this)
    adapter = HeroesCardAdapter(itemClick = {
      SuperHeroDetailActivity.launch(this, it.heroId)
    })
    heroesList.adapter = adapter
  }

  override fun onResume() {
    super.onResume()
    showSuperHeroes().unsafePerformEffects(GetHeroesContext(this, this))
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


fun <A> FreeHeroesAlgebra<A>.unsafePerformEffects(ctx: SuperHeroesContext): Unit {
  val ME = IO.monadError()
  val result: IO<A> = this.foldMap(interpreter(ctx, ME, IO.asyncContext()), ME).ev()
  result.unsafeRunAsync { TODO() }
}