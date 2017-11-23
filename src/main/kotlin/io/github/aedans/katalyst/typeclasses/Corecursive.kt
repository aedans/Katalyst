package io.github.aedans.katalyst.typeclasses

import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.hylo
import kategory.*

interface Corecursive<T> : Typeclass {
    fun <F> embed(t: HK<F, HK<T, F>>, FF: Functor<F>): HK<T, F>

    fun <F, A> ana(a: A, coalg: Coalgebra<F, A>, FF: Functor<F>): HK<T, F> = hylo(a, { embed(it, FF) }, coalg, FF)
}

inline fun <reified F, reified T, A> A.ana(
        FF: Functor<F> = functor(),
        CT: Corecursive<T> = corecursive(),
        noinline coalg: Coalgebra<F, A>
): HK<T, F> = CT.ana(this, coalg, FF)

inline fun <reified F> corecursive(): Corecursive<F> = instance(InstanceParametrizedType(Corecursive::class.java, listOf(typeLiteral<F>())))
