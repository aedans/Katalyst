package io.github.aedans.katalyst.fixedpoint

import arrow.Kind
import arrow.core.*
import arrow.data.NonEmptyList
import arrow.data.nel
import arrow.typeclasses.ComposedFunctor
import arrow.typeclasses.Nested
import arrow.typeclasses.nest
import arrow.typeclasses.unnest
import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.data.ForFix
import io.github.aedans.katalyst.data.ForMu
import io.github.aedans.katalyst.data.ForNu
import io.github.aedans.katalyst.typeclasses.Corecursive
import io.github.aedans.katalyst.typeclasses.Recursive

typealias NonEmptyListPattern<A> = Nested<Tuple2PartialOf<A>, ForOption>

/**
 * Non-empty list parameterized by a recursive type combinator.
 */
typealias GNonEmptyList<T, A> = Kind<T, NonEmptyListPattern<A>>

typealias FixNonEmptyList<A> = GList<ForFix, A>
typealias MuNonEmptyList<A> = GList<ForMu, A>
typealias NuNonEmptyList<A> = GList<ForNu, A>

fun <A> toGNonEmptyListCoalgebra() = Coalgebra<NonEmptyListPattern<A>, NonEmptyList<A>> {
    if (it.tail.isEmpty())
        (it.head toT Option.empty<NonEmptyList<A>>()).nest()
    else
        (it.head toT Option.just(NonEmptyList.fromListUnsafe(it.tail))).nest()
}

fun <A> fromGNonEmptyListAlgebra() = Algebra<NonEmptyListPattern<A>, Eval<NonEmptyList<A>>> {
    val (head, tail) = it.unnest().fix().map { it.fix() }
    tail.fold(
            { Eval.now(head.nel()) },
            { it.map { NonEmptyList(head, it.all) } }
    )
}

inline fun <reified T, A> List<A>.toGNonEmptyList(CT: Corecursive<T>): GList<T, A> = CT.run {
    ana(ComposedFunctor(Option.functor(), Tuple2.functor<A>()), toGListCoalgebra())
}

inline fun <reified T, A> GList<T, A>.toNonEmptyList(RT: Recursive<T>): List<A> = RT.run {
    cata(ComposedFunctor(Option.functor(), Tuple2.functor<A>()), fromGListAlgebra())
}
