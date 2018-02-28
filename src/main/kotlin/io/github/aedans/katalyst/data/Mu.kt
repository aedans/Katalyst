package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.syntax.Algebra
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the least fixed point of a type.
 * This type is the type level encoding of cata.
 */
@higherkind
abstract class Mu<out F> : MuKind<F> {
    abstract fun <A> unMu(fa: Algebra<F, Eval<A>>): Eval<A>
    companion object
}

@instance(Mu::class)
interface MuBirecursiveInstance : Birecursive<MuHK> {
    override fun <F> embedT(t: HK<F, Eval<MuKind<F>>>, FF: Functor<F>): Eval<Mu<F>> =
            Eval.now(object : Mu<F>() {
                override fun <A> unMu(fa: Algebra<F, Eval<A>>) =
                        fa(FF.map(t) { it.flatMap { it.ev().unMu(fa) } })
            })

    override fun <F> projectT(t: MuKind<F>, FF: Functor<F>): HK<F, MuKind<F>> =
        cata(t, { ff -> Eval.later { FF.map(ff) { f -> embedT(FF.map(f.value(), ::Now), FF).value() } } }, FF)

    override fun <F, A> cata(t: MuKind<F>, alg: Algebra<F, Eval<A>>, FF: Functor<F>): A =
            t.ev().unMu(alg).value()
}

@instance(Mu::class)
interface MuRecursiveInstance : Recursive<MuHK>, MuBirecursiveInstance

@instance(Mu::class)
interface MuCorecursiveInstance : Corecursive<MuHK>, MuBirecursiveInstance
