package io.github.aedans.katalyst.typeclasses

import io.github.aedans.katalyst.*
import kategory.*

interface Corecursive<T> : Typeclass {
    fun <F> embed(t: HK<F, HK<T, F>>, FF: Functor<F>): HK<T, F>

    fun <F, A> ana(a: A, coalg: Coalgebra<F, A>,
                   FF: Functor<F>): HK<T, F> =
            hylo(a, { embed(it, FF) }, coalg,
                    FF)

    fun <F, M, A> anaM(a: A, coalgM: CoalgebraM<M, F, A>,
                       TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
            hyloM(a, { MM.pure(embed(it, TF)) }, coalgM,
                    TF, MM)

    fun <F, N, A> gana(a: A, dNF: DistributiveLaw<N, F>, gCoalg: GCoalgebra<N, F, A>,
                       FF: Functor<F>, MN: Monad<N>): HK<T, F> =
            ana(MN.pure(a), { FF.map(dNF.invoke(MN.map(it, gCoalg)), MN::flatten) },
                    FF)

    fun <F, N, M, A> ganaM(a: A, dNF: DistributiveLaw<N, F>, gCoalgM: GCoalgebraM<N, M, F, A>,
                           TF: Traverse<F>, MN: Monad<N>, TN: Traverse<N>, MM: Monad<M>): HK<M, HK<T, F>> =
            ghyloM(a, distributiveLaw(TF, Id.applicative()), dNF, { MM.pure(embed(TF.map(it) { it.ev().value }, TF)) }, gCoalgM,
                    Id.comonad(), Id.traverse(), MN, TN, MM, TF)

    fun <F, A> apo(a: A, gCoalg: GCoalgebra<EitherKindPartial<HK<T, F>>, F, A>,
                   FF: Functor<F>): HK<T, F> =
            hylo(a, { embed(FF.map(it.unnest()) { it.ev().merge() }, FF) }, { gCoalg(it).nest() },
                    ComposedFunctor(FF, Either.functor<HK<T, F>>()))

    fun <F, M, A> apoM(a: A, gCoalgM: GCoalgebraM<EitherKindPartial<HK<T, F>>, M, F, A>,
                       TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
            hyloM(a, { MM.pure(embed(TF.map(TF.map(it.unnest()) { it.ev() }) { it.merge() }, TF)) }, { MM.map(gCoalgM(it)) { it.nest() } },
                    ComposedTraverse(TF, Either.traverse<HK<T, F>>(), Either.applicative()), MM)
}

inline fun <reified F> corecursive(): Corecursive<F> = instance(InstanceParametrizedType(Corecursive::class.java, listOf(typeLiteral<F>())))
