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
abstract class Mu<out F> : MuOf<F> {
    abstract fun <A> unMu(fa: Algebra<F, Eval<A>>): Eval<A>
    companion object
}

@instance(Mu::class)
interface MuBirecursiveInstance : Birecursive<ForMu> {
    override fun <F> embedT(t: Kind<F, Eval<MuOf<F>>>, FF: Functor<F>): Eval<Mu<F>> = FF.run {
        Eval.now(object : Mu<F>() {
            override fun <A> unMu(fa: Algebra<F, Eval<A>>) =
                    fa(t.map { it.flatMap { it.fix().unMu(fa) } })
        })
    }

    override fun <F> projectT(t: MuOf<F>, FF: Functor<F>): Kind<F, MuOf<F>> = FF.run {
        t.cata({ ff ->
            Eval.later {
                ff.map { f ->
                    embedT(f.value().map(::Now), FF).value()
                }
            }
        }, FF)
    }

    override fun <F, A> MuOf<F>.cata(alg: Algebra<F, Eval<A>>, FF: Functor<F>): A =
            fix().unMu(alg).value()
}

@instance(Mu::class)
interface MuRecursiveInstance : Recursive<ForMu>, MuBirecursiveInstance

@instance(Mu::class)
interface MuCorecursiveInstance : Corecursive<ForMu>, MuBirecursiveInstance
