package io.github.aedans.katalyst.data

import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.typeclasses.*
import kategory.*

@higherkind
class Nu<out F>(val a: Any?, val unNu: Coalgebra<F, Any?>) : NuKind<F> {
    companion object {
        // Necessary because of Coalgebra's variance
        @Suppress("UNCHECKED_CAST")
        operator fun <F, A> invoke(a: A, unNu: Coalgebra<F, A>) = Nu(a) { it -> unNu(it as A) }
    }
}

@instance(Nu::class)
interface NuRecursiveInstance : Recursive<NuHK> {
    override fun <F> project(t: NuKind<F>, FF: Functor<F>) = Nu.birecursive().project(t, FF)
}

@instance(Nu::class)
interface NuCorecursiveInstance : Corecursive<NuHK> {
    override fun <F> embed(t: HK<F, NuKind<F>>, FF: Functor<F>) = Nu.birecursive().embed(t, FF)
}

@instance(Nu::class)
interface NuBirecursiveInstance : Birecursive<NuHK> {
    override fun <F> project(t: NuKind<F>, FF: Functor<F>) = t.ev().let { ev ->
        FF.map(ev.unNu(ev.a)) { Nu(it, ev.unNu) }
    }

    override fun <F> embed(t: HK<F, NuKind<F>>, FF: Functor<F>) = Nu.invoke(t) { FF.map(it) { project(it, FF) } }
    override fun <F, A> ana(a: A, coalg: Coalgebra<F, A>, FF: Functor<F>) = Nu(a, coalg)
}
