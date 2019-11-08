package com.github.jorgecastillo.kotlinandroid.io.runtime.cancellation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import arrow.core.Either
import arrow.fx.IOOf
import arrow.fx.fix

fun <A> IOOf<A>.unsafeRunScoped(
        owner: LifecycleOwner,
        cancelAt: Lifecycle.Event = Lifecycle.Event.ON_STOP,
        f: (Either<Throwable, A>) -> Unit
): Unit =
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) Unit
        else {
            val disposable = fix().unsafeRunAsyncCancellable(cb = f)
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == cancelAt) disposable.invoke()
                }
            })
        }
