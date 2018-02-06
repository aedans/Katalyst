package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.Either

@higherkind
data class CoEnv<out E, out W, out A>(val run: Either<E, HK<W, A>>) : CoEnvKind<E, W, A> {
    companion object
}


