package io.github.aedans.katalyst.typeclasses

import arrow.*
import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.syntax.*

/**
 * Typeclass for types that can be generically folded with algebras.
 */
@typeclass
interface Recursive<T> : TC {
    /**
     * Implementation for project.
     */
    fun <F> projectT(t: HK<T, F>, FF: Functor<F>): HK<F, HK<T, F>>

    /**
     * Creates a coalgebra given a functor.
     */
    fun <F> project(FF: Functor<F>): Coalgebra<F, HK<T, F>> =
            { projectT(it, FF) }

    /**
     * Fold generalized over any recursive type.
     */
    fun <F, A> cata(t: HK<T, F>, alg: Algebra<F, Eval<A>>, FF: Functor<F>): A =
            hylo(t, alg, project(FF), FF)
}

///**
// * Cata generalized over a monad.
// */
//fun <T, F, M, A> Recursive<T>.cataM(t: HK<T, F>, alg: AlgebraM<M, F, A>,
//                    TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
//        cata(t, { MM.flatMap(TF.sequence(MM, it), alg) },
//                TF)
//
///**
// * Cata generalized over a comonad.
// */
//fun <T, F, W, A> Recursive<T>.gcata(t: HK<T, F>, dFW: DistributiveLaw<F, W>, alg: GAlgebra<W, F, A>,
//                    FF: Functor<F>, CW: Comonad<W>): A =
//        CW.extract(cata(t, { CW.map(dFW.invoke(FF.map(it, CW::duplicate)), alg) },
//                FF))
//
///**
// * Cata generalized over a monad and a comonad.
// */
//fun <T, F, W, M, A> Recursive<T>.gcataM(t: HK<T, F>, dFW: DistributiveLaw<F, W>, alg: GAlgebraM<W, M, F, A>,
//                        TF: Traverse<F>, TW: Traverse<W>, MM: Monad<M>, CW: Comonad<W>): HK<M, A> =
//        MM.map<HK<W, A>, A>(cataM(t, { TW.traverse(dFW.invoke(TF.map(it, CW::duplicate)), alg, MM) },
//                TF, MM), CW::extract)
//
///**
// * Cata that also provides the previous element.
// */
//fun <T, F, A> Recursive<T>.para(t: HK<T, F>, alg: GAlgebra<PairKWKindPartial<HK<T, F>>, F, A>,
//                FF: Functor<F>): A =
//        hylo(t, { alg(it.unnest()) }, { FF.map(projectT(it, FF), ::square).nest() },
//                ComposedFunctor<F, PairKWKindPartial<HK<T, F>>>(FF, functor()))
//
///**
// * Para generalized over a monad.
// */
//fun <T, F, M, A> Recursive<T>.paraM(t: HK<T, F>, alg: GAlgebraM<PairKWKindPartial<HK<T, F>>, M, F, A>,
//                    TF: Traverse<F>, MM: Monad<M>): HK<M, A> =
//        para(t, { MM.flatMap(TF.sequence(MM, TF.map(it) { traverse<PairKWKindPartial<HK<T, F>>>().sequence(MM, it) }), alg) },
//                TF)
//
///**
// * Cata that also provides all the previous elements.
// */
//fun <T, F, A> Recursive<T>.histo(t: HK<T, F>, alg: GAlgebra<CofreeKindPartial<F>, F, A>,
//                 FF: Functor<F>): A =
//        gcata(t, distHisto(FF), alg,
//                FF, Cofree.comonad())
//
///**
// * Histo generalized over a comonad.
// */
//fun <T, F, W, A> Recursive<T>.ghisto(t: HK<T, F>, dFW: DistributiveLaw<F, W>, alg: GAlgebra<CofreeKindPartial<W>, F, A>,
//                     FF: Functor<F>, FW: Functor<W>): A =
//        gcata(t, distGHisto(dFW, FF, FW), alg,
//                FF, Cofree.comonad())
