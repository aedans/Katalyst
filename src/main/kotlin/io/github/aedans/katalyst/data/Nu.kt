package io.github.aedans.katalyst.data

import arrow.*
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.syntax.Coalgebra
import io.github.aedans.katalyst.typeclasses.*

/**
 * Type level combinator for obtaining the greatest fixed point of a type.
 * This type is the type level encoding of ana.
 */
@higherkind
class Nu<out F>(val a: Any?, val unNu: Coalgebra<F, Any?>) : NuKind<F> {
    companion object {
        // Necessary because of Coalgebra's variance
        @Suppress("UNCHECKED_CAST")
        operator fun <F, A> invoke(a: A, unNu: Coalgebra<F, A>) = Nu(a) { it -> unNu(it as A) }
    }
}

@instance(Nu::class)
interface NuBirecursiveInstance : Birecursive<NuHK> {
    override fun <F> projectT(t: NuKind<F>, FF: Functor<F>) = t.ev().let { ev ->
        FF.map(ev.unNu(ev.a)) { Nu(it, ev.unNu) }
    }

    override fun <F> embedT(t: HK<F, NuKind<F>>, FF: Functor<F>) = Nu.invoke(t) { FF.map(it, project(FF)) }
    override fun <F, A> ana(a: A, coalg: Coalgebra<F, A>, FF: Functor<F>) = Nu(a, coalg)
}

@instance(Nu::class)
interface NuRecursiveInstance : Recursive<NuHK>, NuBirecursiveInstance

@instance(Nu::class)
interface NuCorecursiveInstance : NuBirecursiveInstance
