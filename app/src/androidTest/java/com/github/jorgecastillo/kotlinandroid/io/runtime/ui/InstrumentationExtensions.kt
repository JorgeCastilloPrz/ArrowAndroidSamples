package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import androidx.test.platform.app.InstrumentationRegistry

fun appContext() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
