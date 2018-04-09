@file:Suppress("FunctionName")

package io.github.aedans.katalyst.syntax

import arrow.Kind

fun <F, A> Algebra(it: Algebra<F, A>) = it
fun <M, F, A> AlgebraM(it: AlgebraM<M, F, A>) = it
fun <W, F, A> GAlgebra(it: GAlgebra<W, F, A>) = it
fun <W, M, F, A> GAlgebraM(it: GAlgebraM<W, M, F, A>) = it

fun <F, A> Coalgebra(it: Coalgebra<F, A>) = it
fun <M, F, A> CoalgebraM(it: CoalgebraM<M, F, A>) = it
fun <W, F, A> GCoalgebra(it: GCoalgebra<W, F, A>) = it
fun <W, M, F, A> GCoalgebraM(it: GCoalgebraM<W, M, F, A>) = it

/**
 * Fold over a kind.
 */
typealias Algebra<F, A> = (Kind<F, A>) -> A

/**
 * Algebra generalized over a monad.
 */
typealias AlgebraM<M, F, A> = (Kind<F, A>) -> Kind<M, A>

/**
 * Algebra generalized over a comonad.
 */
typealias GAlgebra<W, F, A> = (Kind<F, Kind<W, A>>) -> A

/**
 * Algebra generalized over a monad and a comonad.
 */
typealias GAlgebraM<W, M, F, A> = (Kind<F, Kind<W, A>>) -> Kind<M, A>

/**
 * Unfold over a kind.
 */
typealias Coalgebra<F, A> = (A) -> Kind<F, A>

/**
 * Coalgebra generalized over a monad.
 */
typealias CoalgebraM<M, F, A> = (A) -> Kind<M, Kind<F, A>>

/**
 * Coalgebra generalized over comonad.
 */
typealias GCoalgebra<W, F, A> = (A) -> Kind<F, Kind<W, A>>

/**
 * Coalgebra generalized over a monad and input wrapped in a comonad.
 */
typealias GCoalgebraM<W, M, F, A> = (A) -> Kind<M, Kind<F, Kind<W, A>>>
