package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically folded with algebras.
 */
@typeclass
interface Recursive<T> : TC {
    /**
     * Implementation for project.
     */
    fun <F> projectT(t: HK<T, F>, FF: Functor<F>): HK<F, HK<T, F>>

    /**
     * Creates a coalgebra given a functor.
     */
    fun <F> project(FF: Functor<F>): Coalgebra<F, HK<T, F>> =
            { projectT(it, FF) }

    /**
     * Fold generalized over any recursive type.
     */
    fun <F, A> cata(t: HK<T, F>, alg: Algebra<F, Eval<A>>, FF: Functor<F>): A =
            hylo(t, alg, project(FF), FF)
}
