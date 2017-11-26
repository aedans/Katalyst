package io.github.aedans.katalyst.typeclasses

import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.CoalgebraM
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.hyloM
import kategory.*

interface Corecursive<T> : Typeclass {
    fun <F> embed(t: HK<F, HK<T, F>>, FF: Functor<F>): HK<T, F>

    fun <F, A> ana(a: A, coalg: Coalgebra<F, A>, FF: Functor<F>): HK<T, F> =
            hylo(a, { embed(it, FF) }, coalg, FF)

    fun <M, F, A> anaM(a: A, coalgM: CoalgebraM<M, F, A>, TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
            hyloM(a, { MM.pure(embed(it, TF)) }, coalgM, TF, MM)
}

inline fun <reified F, reified T, A> A.ana(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        noinline coalg: Coalgebra<F, A>
): HK<T, F> = CT.ana(this, coalg, FF)

inline fun <reified M, reified F, reified T, A> A.anaM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline coalgM: CoalgebraM<M, F, A>
): HK<M, HK<T, F>> = CT.anaM(this, coalgM, TF, MM)

inline fun <reified F> corecursive(): Corecursive<F> = instance(InstanceParametrizedType(Corecursive::class.java, listOf(typeLiteral<F>())))
