package io.github.aedans.katalyst.fixedpoint

import arrow.Kind
import arrow.core.*
import arrow.free.Cofree
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*
import io.github.aedans.katalyst.typeclasses.*

typealias CofreePattern<S, A> = EnvTPartialOf<A, S>

/**
 * Cofree comonad parameterized by a recursive type combinator.
 */
typealias GCofree<T, S, A> = Kind<T, CofreePattern<S, A>>

typealias FixCofree<S, A> = GCofree<ForFix, S, A>
typealias MuCofree<S, A> = GCofree<ForMu, S, A>
typealias NuCofree<S, A> = GCofree<ForNu, S, A>

fun <S, A> toGCofreeCoalgebra() = Coalgebra<CofreePattern<S, A>, Cofree<S, A>> {
    EnvT(it.head toT it.tailForced())
}

fun <S, A> fromGCofreeAlgebra(FS: Functor<S>) = FS.run {
    Algebra<CofreePattern<S, A>, Eval<Cofree<S, A>>> {
        val fix = it.fix()
        Eval.now(Cofree(FS, fix.ask, Eval.later { fix.lower.map { it.value() } }))
    }
}

inline fun <reified T, S, A> Cofree<S, A>.toGCofree(CT: Corecursive<T>, FS: Functor<S>): GCofree<T, S, A> = CT.run {
    ana(toGCofreeCoalgebra(), EnvT.functor<A, S>(FS))
}

inline fun <reified T, S, A> GCofree<T, S, A>.toCofree(RT: Recursive<T>, FS: Functor<S>): Cofree<S, A> = RT.run {
    cata(fromGCofreeAlgebra(FS), EnvT.functor<A, S>(FS))
}
