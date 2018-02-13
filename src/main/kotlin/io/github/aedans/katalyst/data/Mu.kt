package io.github.aedans.katalyst.data

import arrow.*
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.syntax.Algebra
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the least fixed point of a type.
 * This type is the type level encoding of cata.
 */
@higherkind
abstract class Mu<out F> : MuKind<F> {
    abstract fun <A> unMu(fa: Algebra<F, A>): A
    companion object
}

@instance(Mu::class)
interface MuBirecursiveInstance : Birecursive<MuHK> {
    override fun <F> embedT(t: HK<F, MuKind<F>>, FF: Functor<F>) = object : Mu<F>() {
        override fun <A> unMu(fa: Algebra<F, A>) = fa(FF.map(t) { cata(it, fa, FF) })
    }

    override fun <F> projectT(t: MuKind<F>, FF: Functor<F>): HK<F, MuKind<F>> = cata(t, { FF.map(it, embed(FF)) }, FF)
    override fun <F, A> cata(t: MuKind<F>, alg: Algebra<F, A>, FF: Functor<F>): A = t.ev().unMu(alg)
}

@instance(Mu::class)
interface MuRecursiveInstance : Recursive<MuHK>, MuBirecursiveInstance

@instance(Mu::class)
interface MuCorecursiveInstance : Corecursive<MuHK>, MuBirecursiveInstance
