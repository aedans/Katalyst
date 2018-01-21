package io.github.aedans.katalyst.data

import arrow.*
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.typeclasses.*

@higherkind
data class Fix<out A>(val unfix: HK<A, FixKind<A>>) : FixKind<A> {
    companion object
}

@instance(Fix::class)
interface FixRecursiveInstance : Recursive<FixHK> {
    override fun <F> project(t: FixKind<F>, FF: Functor<F>) = Fix.birecursive().project(t, FF)
}

@instance(Fix::class)
interface FixCorecursiveInstance : Corecursive<FixHK> {
    override fun <F> embed(t: HK<F, FixKind<F>>, FF: Functor<F>) = Fix.birecursive().embed(t, FF)
}

@instance(Fix::class)
interface FixBirecursiveInstance : Birecursive<FixHK> {
    override fun <F> project(t: FixKind<F>, FF: Functor<F>) = t.ev().unfix
    override fun <F> embed(t: HK<F, FixKind<F>>, FF: Functor<F>) = Fix(t)
}
