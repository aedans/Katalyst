package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*

typealias ListPattern<A> = MaybeAndKindPartial<A>

/**
 * Linked list parameterized by a recursive type combinator.
 */
typealias GList<T, A> = HK<T, ListPattern<A>>

typealias FixList<A> = GList<FixHK, A>
typealias MuList<A> = GList<MuHK, A>
typealias NuList<A> = GList<NuHK, A>

fun <A> toGListCoalgebra() = Coalgebra<ListPattern<A>, List<A>> {
    if (it.isEmpty()) MaybeAnd.empty else MaybeAnd(it.first(), it.drop(1))
}

fun <A> fromGListAlgebra() = Algebra<ListPattern<A>, List<A>> {
    it.ev().value.fold({ emptyList() }, { listOf(it.a) + it.b })
}

inline fun <reified T, A> List<A>.toGList(): GList<T, A> = ana(coalg = toGListCoalgebra())
inline fun <reified T, A> GList<T, A>.toList(): List<A> = cata(alg = fromGListAlgebra())
