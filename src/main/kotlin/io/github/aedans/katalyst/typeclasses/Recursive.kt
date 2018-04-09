package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T> {
    /**
     * Implementation for project.
     */
    fun <F> projectT(t: Kind<T, F>, FF: Functor<F>): Kind<F, Kind<T, F>>

    /**
     * Creates a coalgebra given a functor.
     */
    fun <F> project(FF: Functor<F>): Coalgebra<F, Kind<T, F>> =
            { projectT(it, FF) }

    /**
     * Fold generalized over any recursive type.
     */
    fun <F, A> Kind<T, F>.cata(alg: Algebra<F, Eval<A>>, FF: Functor<F>): A =
            hylo(this, alg, project(FF), FF)
}
