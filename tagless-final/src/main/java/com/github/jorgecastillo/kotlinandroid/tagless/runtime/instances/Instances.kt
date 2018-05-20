package com.github.jorgecastillo.kotlinandroid.tagless.runtime.instances

import arrow.dagger.effects.instances.ArrowEffectsInstances
import arrow.dagger.instances.ArrowInstances
import arrow.effects.ForIO
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.Presentation
import dagger.Component

@Component(modules = [
    ArrowInstances::class,
    ArrowEffectsInstances::class
])
interface Instances {
    fun presentation(): Presentation<ForIO>
}

fun instances(): Instances = DaggerInstances.create()