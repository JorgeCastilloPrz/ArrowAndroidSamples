package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.github.jorgecastillo.kotlinandroid.io.runtime.ui.NewsItemDetailActivity

fun <F> Runtime<F>.goToNewsItemDetail(ctx: Context, title: String): Kind<F, Unit> = fx.concurrent {
    !effect { NewsItemDetailActivity.launch(ctx, title) }
}
