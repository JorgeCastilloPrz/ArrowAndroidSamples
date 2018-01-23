package com.github.jorgecastillo.kotlinandroid.tagless.runtime.instances

import arrow.effects.IO
import arrow.effects.IOHK
import arrow.instance
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence.DataSource
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence.HeroesRepository
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.Navigation
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.Presentation

typealias TargetRuntime = IOHK

@instance(IO::class)
interface IOPresentationInstance<F> : Presentation<F>

@instance(IO::class)
interface IONavigationInstance<F> : Navigation<F>

@instance(IO::class)
interface IOHeroesUseCasesInstance<F> : HeroesUseCases<F>

@instance(IO::class)
interface IOHeroesRepositoryInstance<F> : HeroesRepository<F>

@instance(IO::class)
interface IODataSourceInstance<F> : DataSource<F>
