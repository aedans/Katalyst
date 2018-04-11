package io.github.aedans.katalyst.fixedpoint

import arrow.Kind
import arrow.core.*
import arrow.syntax.collections.prependTo
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

typealias ListPattern<A> = Nested<ForOption, Tuple2PartialOf<A>>

/**
 * List parameterized by a recursive type combinator.
 */
typealias GList<T, A> = Kind<T, ListPattern<A>>

typealias FixList<A> = GList<ForFix, A>
typealias MuList<A> = GList<ForMu, A>
typealias NiList<A> = GList<ForNu, A>

fun <A> toGListCoalgebra() = Coalgebra<ListPattern<A>, List<A>> {
    if (it.isEmpty())
        Option.empty<Tuple2Of<A, List<A>>>().nest()
    else
        Option.just<Tuple2Of<A, List<A>>>(it.first() toT it.drop(1)).nest()
}

fun <A> fromGListAlgebra() = Algebra<ListPattern<A>, Eval<List<A>>> {
    val fix = it.unnest().fix().map { it.fix() }
    fix.fold(
            { Eval.now(emptyList()) },
            { (head, tail) -> tail.map { head prependTo it } }
    )
}

inline fun <reified T, A> List<A>.toGList(CT: Corecursive<T>): GList<T, A> = CT.run {
    ana(toGListCoalgebra(), ComposedFunctor(Option.functor(), Tuple2.functor<A>()))
}

inline fun <reified T, A> GList<T, A>.toCofree(RT: Recursive<T>): List<A> = RT.run {
    cata(fromGListAlgebra(), ComposedFunctor(Option.functor(), Tuple2.functor<A>()))
}
