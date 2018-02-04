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
interface MuRecursiveInstance : Recursive<MuHK> {
    override fun <F> project(t: MuKind<F>, FF: Functor<F>) = Mu.birecursive().project(t, FF)
}

@instance(Mu::class)
interface MuCorecursiveInstance : Corecursive<MuHK> {
    override fun <F> embed(t: HK<F, MuKind<F>>, FF: Functor<F>) = Mu.birecursive().embed(t, FF)
}

@instance(Mu::class)
interface MuBirecursiveInstance : Birecursive<MuHK> {
    override fun <F> embed(t: HK<F, MuKind<F>>, FF: Functor<F>) = object : Mu<F>() {
        override fun <A> unMu(fa: Algebra<F, A>) = fa(FF.map(t) { cata(it, fa, FF) })
    }

    override fun <F> project(t: MuKind<F>, FF: Functor<F>): HK<F, MuKind<F>> = cata(t, { FF.map(it) { embed(it, FF) } }, FF)
    override fun <F, A> cata(t: MuKind<F>, alg: Algebra<F, A>, FF: Functor<F>): A = t.ev().unMu(alg)
}
