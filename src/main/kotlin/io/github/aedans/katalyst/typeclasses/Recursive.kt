package io.github.aedans.katalyst.typeclasses

import io.github.aedans.katalyst.*
import kategory.*

interface Recursive<T> : Typeclass {
    fun <F> project(t: HK<T, F>, FF: Functor<F>): HK<F, HK<T, F>>

    fun <F, A> cata(t: HK<T, F>, alg: Algebra<F, A>,
                    FF: Functor<F>): A =
            hylo(t, alg, { project(it, FF) }, FF)

    fun <F, M, A> cataM(t: HK<T, F>, algM: AlgebraM<M, F, A>,
                        TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
            cata(t, { MM.flatMap(TF.sequence(MM, it), algM) }, TF)

    fun <F, W, A> gcata(t: HK<T, F>, dFW: DistributiveLaw<F, W>, gAlg: GAlgebra<W, F, A>,
                        FF: Functor<F>, CW: Comonad<W>): A =
            CW.extract(cata(t, { CW.map(dFW.invoke(FF.map(it, CW::duplicate)), gAlg) }, FF))

    fun <F, W, M, A> gcataM(t: HK<T, F>, dFW: DistributiveLaw<F, W>, gAlgM: GAlgebraM<W, M, F, A>,
                            TF: Traverse<F>, TW: Traverse<W>, MM: Monad<M>, CW: Comonad<W>): HK<M, A> =
            MM.map<HK<W, A>, A>(cataM(t, { TW.traverse(dFW.invoke(TF.map(it, CW::duplicate)), gAlgM, MM) }, TF, MM)) { CW.extract(it) }
}

inline fun <reified F> recursive(): Recursive<F> = instance(InstanceParametrizedType(Recursive::class.java, listOf(typeLiteral<F>())))
