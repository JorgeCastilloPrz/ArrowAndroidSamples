package com.github.jorgecastillo.kotlinandroid.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.jorgecastillo.kotlinandroid.R.layout
import com.github.jorgecastillo.kotlinandroid.R.string
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroDetailView
import com.github.jorgecastillo.kotlinandroid.presentation.getSuperHeroDetails
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import kotlinx.android.synthetic.main.activity_detail.appBar
import kotlinx.android.synthetic.main.activity_detail.collapsingToolbar
import kotlinx.android.synthetic.main.activity_detail.description
import kotlinx.android.synthetic.main.activity_detail.headerImage

class SuperHeroDetailActivity : AppCompatActivity(), SuperHeroDetailView {

  companion object {
    val EXTRA_HERO_ID = "EXTRA_HERO_ID"

    fun launch(source: Context, heroId: String) {
      val intent = Intent(source, SuperHeroDetailActivity::class.java)
      intent.putExtra(EXTRA_HERO_ID, heroId)
      source.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_detail)
  }

  override fun onResume() {
    super.onResume()
    intent.extras?.let {
      val heroId = it.getString(EXTRA_HERO_ID)
      getSuperHeroDetails(heroId).run(GetHeroDetailsContext(this, this))
    } ?: closeWithError()
  }

  private fun closeWithError() {
    Toast.makeText(this, string.hero_id_needed, Toast.LENGTH_SHORT).show()
  }

  override fun drawHero(hero: SuperHeroViewModel) {
    collapsingToolbar.title = hero.name
    description.text = hero.description.let { if (it.isNotEmpty()) it else getString(string.empty_description) }
    headerImage.loadImageAsync(hero.photoUrl)
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
