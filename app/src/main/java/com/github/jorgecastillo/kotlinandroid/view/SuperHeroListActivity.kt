package com.github.jorgecastillo.kotlinandroid.view

import android.os.Bundle
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesReaderContext
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.github.jorgecastillo.kotlinandroid.presentation.getSuperHeroes
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SuperHeroListActivity : JobDispatcherActivity(), SuperHeroesView {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()
    launch(contextJob + UI) {
      val heroes = getSuperHeroes().run(GetHeroesReaderContext(this@SuperHeroListActivity))
    }
  }

  override fun drawHeroes(heroes: NonEmptyList<SuperHeroViewModel>) {
    resultText.text = "Heros loaded!"
  }

  override fun showHeroesNotFoundError() {
  }

  override fun showServerError() {
  }
}
