package io.github.aedans.katalyst.typeclasses

import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.AlgebraM
import io.github.aedans.katalyst.hylo
import kategory.*

interface Recursive<T> : Typeclass {
    fun <F> project(t: HK<T, F>, FF: Functor<F>): HK<F, HK<T, F>>

    fun <F, A> cata(t: HK<T, F>, alg: Algebra<F, A>, FF: Functor<F>): A =
            hylo(t, alg, { project(it, FF) }, FF)

    fun <M, F, A> cataM(t: HK<T, F>, algM: AlgebraM<M, F, A>, TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
            cata(t, { MM.flatMap(TF.sequence(MM, it), algM) }, TF)
}

inline fun <reified F, reified T, A> HK<T, F>.cata(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, A>
): A = RT.cata(this, alg, FF)

inline fun <reified M, reified F, reified T, A> HK<T, F>.cataM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline algM: AlgebraM<M, F, A>
): HK<M, A> = RT.cataM(this, algM, TF, MM)

inline fun <reified F> recursive(): Recursive<F> = instance(InstanceParametrizedType(Recursive::class.java, listOf(typeLiteral<F>())))
