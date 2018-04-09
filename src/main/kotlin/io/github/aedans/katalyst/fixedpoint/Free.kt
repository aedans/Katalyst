package io.github.aedans.katalyst.fixedpoint

import arrow.Kind
import arrow.core.*
import arrow.free.*
import arrow.free.Free.*
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.typeclasses.*

typealias FreePattern<S, A> = CoEnvPartialOf<A, S>

/**
 * Free monad parameterized by a recursive type combinator.
 */
typealias GFree<T, S, A> = Kind<T, FreePattern<S, A>>

typealias FixFree<S, A> = GFree<ForFix, S, A>
typealias MuFree<S, A> = GFree<ForMu, S, A>
typealias NuFree<S, A> = GFree<ForNu, S, A>

fun <S, A> toGFreeCoalgebra(FS: Functor<S>) = FS.run {
    Coalgebra<FreePattern<S, A>, Free<S, A>> { free ->
        tailrec fun runStep(free: Free<S, A>): Kind<FreePattern<S, A>, Free<S, A>> = when (free) {
            is Pure -> CoEnv(Left(free.a))
            is Suspend -> CoEnv(Right(free.a.map { it.free<S, A>() }))
            else -> runStep(free.step())
        }

        runStep(free)
    }
}

fun <S, A> fromGFreeAlgebra() = Algebra<FreePattern<S, A>, Eval<Free<S, A>>> {
    Eval.now(it.fix().run.fold(
            Free.Companion::just,
            { ssa -> Free.liftF(ssa).fix().flatMap { it.value() } }
    ))
}

inline fun <reified T, S, A> Free<S, A>.toGFree(CT: Corecursive<T>, FS: Functor<S>): GFree<T, S, A> = CT.run {
    ana(toGFreeCoalgebra(FS), CoEnv.functor<A, S>(FS))
}

inline fun <reified T, S, A> GFree<T, S, A>.toFree(RT: Recursive<T>, FS: Functor<S>): Free<S, A> = RT.run {
    cata(fromGFreeAlgebra(), CoEnv.functor<A, S>(FS))
}
