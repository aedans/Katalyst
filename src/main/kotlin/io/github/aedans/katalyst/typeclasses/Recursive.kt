package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.free.*
import arrow.instances.ComposedFunctor
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically folded with algebras.
 */
@typeclass
interface Recursive<T> : TC {
    /**
     * Creates a coalgebra given a functor.
     */
    fun <F> project(FF: Functor<F>): Coalgebra<F, HK<T, F>> = { t: HK<T, F> -> projectT(t, FF) }

    /**
     * Implementation for project.
     */
    fun <F> projectT(t: HK<T, F>, FF: Functor<F>): HK<F, HK<T, F>>

    /**
     * Fold generalized over any recursive type.
     */
    fun <F, A> cata(t: HK<T, F>, alg: Algebra<F, A>,
                    FF: Functor<F>): A =
            hylo(t, alg, project(FF),
                    FF)

    /**
     * Cata generalized over a monad.
     */
    fun <F, M, A> cataM(t: HK<T, F>, algM: AlgebraM<M, F, A>,
                        TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
            cata(t, { MM.flatMap(TF.sequence(MM, it), algM) },
                    TF)

    /**
     * Cata generalized over a comonad.
     */
    fun <F, W, A> gcata(t: HK<T, F>, dFW: DistributiveLaw<F, W>, gAlg: GAlgebra<W, F, A>,
                        FF: Functor<F>, CW: Comonad<W>): A =
            CW.extract(cata(t, { CW.map(dFW.invoke(FF.map(it, CW::duplicate)), gAlg) },
                    FF))

    /**
     * Cata generalized over a monad and a comonad.
     */
    fun <F, W, M, A> gcataM(t: HK<T, F>, dFW: DistributiveLaw<F, W>, gAlgM: GAlgebraM<W, M, F, A>,
                            TF: Traverse<F>, TW: Traverse<W>, MM: Monad<M>, CW: Comonad<W>): HK<M, A> =
            MM.map<HK<W, A>, A>(cataM(t, { TW.traverse(dFW.invoke(TF.map(it, CW::duplicate)), gAlgM, MM) },
                    TF, MM), CW::extract)

    /**
     * Cata that also provides the previous element.
     */
    fun <F, A> para(t: HK<T, F>, gAlg: GAlgebra<PairKWKindPartial<HK<T, F>>, F, A>,
                    FF: Functor<F>): A =
            hylo(t, { gAlg(it.unnest()) }, { FF.map(projectT(it, FF), ::square).nest() },
                    ComposedFunctor<F, PairKWKindPartial<HK<T, F>>>(FF, functor()))

    /**
     * Para generalized over a monad.
     */
    fun <F, M, A> paraM(t: HK<T, F>, gAlg: GAlgebraM<PairKWKindPartial<HK<T, F>>, M, F, A>,
                        TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
            para(t, { MM.flatMap(TF.sequence(MM, TF.map(it) { traverse<PairKWKindPartial<HK<T, F>>>().sequence(MM, it) }), gAlg) },
                    TF)

    /**
     * Cata that also provides all the previous elements.
     */
    fun <F, A> histo(t: HK<T, F>, gAlg: GAlgebra<CofreeKindPartial<F>, F, A>,
                     FF: Functor<F>): A =
            gcata(t, distHisto(FF), gAlg, FF, Cofree.comonad())

    /**
     * Histo generalized over a comonad.
     */
    fun <F, W, A> ghisto(t: HK<T, F>, dFW: DistributiveLaw<F, W>, gAlg: GAlgebra<CofreeKindPartial<W>, F, A>,
                         FF: Functor<F>, FW: Functor<W>): A =
            gcata(t, distGHisto(dFW, FF, FW), gAlg, FF, Cofree.comonad())
}
