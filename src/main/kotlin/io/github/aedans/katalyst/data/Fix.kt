package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the fixed point of a type.
 * This type is the type level encoding of primitive recursion.
 */
@higherkind
data class Fix<out A>(val unfix: HK<A, Eval<FixKind<A>>>) : FixKind<A> {
    companion object
}

@instance(Fix::class)
interface FixBirecursiveInstance : Birecursive<FixHK> {
    override fun <F> projectT(t: FixKind<F>, FF: Functor<F>) =
            FF.map(t.ev().unfix) { it.value() }

    override fun <F> embedT(t: HK<F, Eval<FixKind<F>>>, FF: Functor<F>) =
            Eval.later { Fix(t) }
}

@instance(Fix::class)
interface FixRecursiveInstance : Recursive<FixHK>, FixBirecursiveInstance

@instance(Fix::class)
interface FixCorecursiveInstance : Corecursive<FixHK>, FixBirecursiveInstance
