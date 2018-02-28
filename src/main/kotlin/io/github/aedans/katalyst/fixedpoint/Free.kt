package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import arrow.core.*
import arrow.free.*
import arrow.free.Free.*
import arrow.typeclasses.Functor
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

fun <S, A> toGFreeCoalgebra(FS: Functor<S>) = Coalgebra<FreePattern<S, A>, Free<S, A>> { free ->
    tailrec fun runStep(free: Free<S, A>): HK<FreePattern<S, A>, Free<S, A>> = when (free) {
        is Pure -> CoEnv(Left(free.a))
        is Suspend -> CoEnv(Right(FS.map(free.a) { it.free<S, A>() }))
        else -> runStep(free.step())
    }

    runStep(free)
}

fun <S, A> fromGFreeAlgebra() = Algebra<FreePattern<S, A>, Eval<Free<S, A>>> {
    Eval.now(it.ev().run.fold(
            Free.Companion::pure,
            { ssa -> Free.liftF(ssa).ev().flatMap { it.value() } }
    ))
}

inline fun <reified T, S, A> Free<S, A>.toGFree(FS: Functor<S>): GFree<T, S, A> = ana(coalg = toGFreeCoalgebra(FS))
inline fun <reified T, S, A> GFree<T, S, A>.toFree(): Free<S, A> = cata(alg = fromGFreeAlgebra())
