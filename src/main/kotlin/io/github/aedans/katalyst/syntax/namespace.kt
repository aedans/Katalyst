package io.github.aedans.katalyst.syntax

import arrow.HK
import arrow.core.Eval
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.typeclasses.*

inline fun <reified F, reified T, A> HK<T, F>.cata(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, Eval<A>>
) = RT.cata(this, alg, FF)

inline fun <reified F, reified T, A> A.ana(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        noinline coalg: Coalgebra<F, A>
) = CT.ana(this, coalg, FF)

inline fun <reified F, A, B> A.hylo(
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, Eval<B>>,
        noinline coalg: Coalgebra<F, A>
) = hylo(this, alg, coalg, FF)

inline fun <reified F, reified G> distributiveLaw() = distributiveLaw(traverse<F>(), applicative<G>())

inline fun <reified F, reified T> project(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor()
) = RT.project(FF)

inline fun <reified F, reified T> embed(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor()
) = CT.embed(FF)
