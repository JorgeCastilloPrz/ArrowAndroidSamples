package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.lang.ReaderContext
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView

class GetHeroesReaderContext(val view: SuperHeroesView) : ReaderContext, InteractorReaderContext()
