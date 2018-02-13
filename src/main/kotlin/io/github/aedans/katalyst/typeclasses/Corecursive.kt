package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.core.*
import arrow.free.*
import arrow.instances.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
@typeclass
interface Corecursive<T> : TC {
    /**
     * Implementation for embed.
     */
    fun <F> embedT(t: HK<F, HK<T, F>>, FF: Functor<F>): HK<T, F>

    /**
     * Creates a algebra given a functor.
     */
    fun <F> embed(FF: Functor<F>): Algebra<F, HK<T, F>> = { t: HK<F, HK<T, F>> -> embedT(t, FF) }

    /**
     * Unfold into any recursive type.
     */
    fun <F, A> ana(a: A, coalg: Coalgebra<F, A>,
                   FF: Functor<F>): HK<T, F> =
            hylo(a, embed(FF), coalg,
                    FF)
}

/**
 * Ana generalized over a monad.
 */
fun <T, F, M, A> Corecursive<T>.anaM(a: A, coalg: CoalgebraM<M, F, A>,
                   TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
        hyloM(a, { MM.pure(embedT(it, TF)) }, coalg,
                TF, MM)

/**
 * Ana generalized over a comonad.
 */
fun <T, F, N, A> Corecursive<T>.gana(a: A, dNF: DistributiveLaw<N, F>, coalg: GCoalgebra<N, F, A>,
                   FF: Functor<F>, MN: Monad<N>): HK<T, F> =
        ana(MN.pure(a), { FF.map(dNF.invoke(MN.map(it, coalg)), MN::flatten) },
                FF)

/**
 * Ana generalized over a monad and a comonad.
 */
fun <T, F, N, M, A> Corecursive<T>.ganaM(a: A, dNF: DistributiveLaw<N, F>, coalg: GCoalgebraM<N, M, F, A>,
                       TF: Traverse<F>, MN: Monad<N>, TN: Traverse<N>, MM: Monad<M>): HK<M, HK<T, F>> =
        ghyloM(a, distCata(TF), dNF, { MM.pure(embedT(TF.map(it) { it.ev().value }, TF)) }, coalg,
                Id.comonad(), Id.traverse(), MN, TN, MM, TF)

/**
 * Ana that stops on failure.
 */
fun <T, F, A> Corecursive<T>.apo(a: A, coalg: GCoalgebra<EitherKindPartial<HK<T, F>>, F, A>,
               FF: Functor<F>): HK<T, F> =
        hylo(a, { embedT(FF.map(it.unnest()) { it.ev().merge() }, FF) }, { coalg(it).nest() },
                ComposedFunctor(FF, Either.functor<HK<T, F>>()))

/**
 * Apo generalized over a monad.
 */
fun <T, F, M, A> Corecursive<T>.apoM(a: A, coalg: GCoalgebraM<EitherKindPartial<HK<T, F>>, M, F, A>,
                   TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
        hyloM(a, { MM.pure(embedT(TF.map(TF.map(it.unnest()) { it.ev() }) { it.merge() }, TF)) }, { MM.map(coalg(it)) { it.nest() } },
                ComposedTraverse(TF, Either.traverse<HK<T, F>>(), Either.applicative()), MM)

/**
 * Ana that also computes future elements.
 */
fun <T, F, A> Corecursive<T>.futu(a: A, coalg: GCoalgebra<FreeKindPartial<F>, F, A>,
                FF: Functor<F>): HK<T, F> =
        gana(a, distFutu(FF), coalg,
                FF, Free.monad())

/**
 * Futu generalized over a monad.
 */
fun <T, F, M, A> Corecursive<T>.futuM(a: A, coalg: GCoalgebraM<FreeKindPartial<F>, M, F, A>,
                    MF: Monad<F>, TF: Traverse<F>, MM: Monad<M>): HK<M, HK<T, F>> =
        ganaM(a, distFutu(MF), coalg,
                TF, Free.monad(), /* Free.traverse() */ TODO("Free.traverse()"), MM)
