package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
@typeclass
interface Corecursive<T> : TC {
    /**
     * Implementation for embed.
     */
    fun <F> embedT(t: HK<F, Eval<HK<T, F>>>, FF: Functor<F>): Eval<HK<T, F>>

    /**
     * Creates a algebra given a functor.
     */
    fun <F> embed(FF: Functor<F>): Algebra<F, Eval<HK<T, F>>> = { embedT(it, FF) }

    /**
     * Unfold into any recursive type.
     */
    fun <F, A> ana(a: A, coalg: Coalgebra<F, A>, FF: Functor<F>): HK<T, F> = hylo(a, embed(FF), coalg, FF)
}
