package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.syntax.Coalgebra
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the greatest fixed point of a type.
 * This type is the type level encoding of ana.
 */
@higherkind
class Nu<out F>(val a: Any?, val unNu: Coalgebra<F, Any?>) : NuOf<F> {
    companion object {
        // Necessary because of Coalgebra's variance
        @Suppress("UNCHECKED_CAST")
        operator fun <F, A> invoke(a: A, unNu: Coalgebra<F, A>) = Nu(a) { it -> unNu(it as A) }
    }
}

@instance(Nu::class)
interface NuBirecursiveInstance : Birecursive<ForNu> {
    override fun <F> projectT(t: NuOf<F>, FF: Functor<F>): Kind<F, Nu<F>> = FF.run {
        val fix = t.fix()
        val unNu = fix.unNu
        unNu(fix.a).map { Nu(it, unNu) }
    }

    override fun <F> embedT(t: Kind<F, Eval<NuOf<F>>>, FF: Functor<F>) = FF.run {
        Eval.now(Nu.invoke(t) { f -> f.map { nu -> projectT(nu.value(), FF).map(::Now) } })
    }

    override fun <F, A> A.ana(coalg: Coalgebra<F, A>, FF: Functor<F>) =
            Nu(this, coalg)
}

@instance(Nu::class)
interface NuRecursiveInstance : Recursive<ForNu>, NuBirecursiveInstance

@instance(Nu::class)
interface NuCorecursiveInstance : Corecursive<ForNu>, NuBirecursiveInstance
