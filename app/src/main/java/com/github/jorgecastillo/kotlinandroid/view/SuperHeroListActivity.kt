package com.github.jorgecastillo.kotlinandroid.view

import android.os.Bundle
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.presentation.getSuperHeroes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SuperHeroListActivity : JobDispatcherActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()
    launch(contextJob + UI) {
      getSuperHeroes()
      resultText.text = "Heros loaded!"
    }
  }
}
