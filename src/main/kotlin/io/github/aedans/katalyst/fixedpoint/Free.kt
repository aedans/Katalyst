package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import arrow.core.*
import arrow.free.*
import arrow.free.Free.Pure
import arrow.typeclasses.*
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*

typealias FreePattern<S, A> = CoEnvKindPartial<A, S>

/**
 * Free monad parameterized by a recursive type combinator.
 */
typealias GFree<T, S, A> = HK<T, FreePattern<S, A>>

typealias FixFree<S, A> = GFree<FixHK, S, A>
typealias MuFree<S, A> = GFree<MuHK, S, A>
typealias NuFree<S, A> = GFree<NuHK, S, A>

fun <S, A> toGFreeCoalgebra(MS: Monad<S>) = Coalgebra<FreePattern<S, A>, Free<S, A>> { free ->
    when (free) {
        is Pure -> CoEnv(Left(free.a))
        else -> CoEnv(Right(MS.map(free.run(MS)) { free.step() }))
    }
}

fun <S, A> fromGFreeAlgebra() = Algebra<FreePattern<S, A>, Free<S, A>> {
    it.ev().run.fold(
            Free.Companion::pure,
            { Free.monad<S>().flatten(Free.liftF(it)).ev() }
    )
}

inline fun <reified T, reified S, A> Free<S, A>.toGFree(MS: Monad<S> = monad()): GFree<T, S, A> = ana(coalg = toGFreeCoalgebra(MS))
inline fun <reified T, S, A> GFree<T, S, A>.toFree(): Free<S, A> = cata(alg = fromGFreeAlgebra())
