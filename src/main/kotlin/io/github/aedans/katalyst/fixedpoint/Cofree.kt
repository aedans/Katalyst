package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import arrow.core.*
import arrow.free.Cofree
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*

typealias CofreePattern<S, A> = EnvTKindPartial<A, S>

/**
 * Cofree comonad parameterized by a recursive type combinator.
 */
typealias GCofree<T, S, A> = HK<T, CofreePattern<S, A>>

typealias FixCofree<S, A> = GCofree<FixHK, S, A>
typealias MuCofree<S, A> = GCofree<MuHK, S, A>
typealias NuCofree<S, A> = GCofree<NuHK, S, A>

fun <S, A> toGCofreeCoalgebra() = Coalgebra<CofreePattern<S, A>, Cofree<S, A>> {
    EnvT(it.head toT it.tailForced())
}

fun <S, A> fromGCofreeAlgebra(FS: Functor<S>) = Algebra<CofreePattern<S, A>, Eval<Cofree<S, A>>> {
    val ev = it.ev()
    Eval.now(Cofree(FS, ev.ask, Eval.later { FS.map(ev.lower) { it.value() } }))
}

inline fun <reified T, S, A> Cofree<S, A>.toGCofree(): GCofree<T, S, A> = ana(coalg = toGCofreeCoalgebra())
inline fun <reified T, S, A> GCofree<T, S, A>.toCofree(FS: Functor<S>): Cofree<S, A> = cata(alg = fromGCofreeAlgebra(FS))
