package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.extensions

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.loadImageAsync(url: String) {
  Picasso.with(context).load(url).into(this)
}
