package io.github.aedans.katalyst.data

import arrow.*
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the fixed point of a type.
 * This type is the type level encoding of primitive recursion.
 */
@higherkind
data class Fix<out A>(val unfix: HK<A, FixKind<A>>) : FixKind<A> {
    companion object
}

@instance(Fix::class)
interface FixBirecursiveInstance : Birecursive<FixHK> {
    override fun <F> projectT(t: FixKind<F>, FF: Functor<F>) = t.ev().unfix
    override fun <F> embedT(t: HK<F, FixKind<F>>, FF: Functor<F>) = Fix(t)
}

@instance(Fix::class)
interface FixRecursiveInstance : Recursive<FixHK>, FixBirecursiveInstance

@instance(Fix::class)
interface FixCorecursiveInstance : Corecursive<FixHK>, FixBirecursiveInstance
