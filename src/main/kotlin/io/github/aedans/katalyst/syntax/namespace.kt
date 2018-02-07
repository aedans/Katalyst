package io.github.aedans.katalyst.syntax

import arrow.HK
import arrow.core.EitherKindPartial
import arrow.free.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.typeclasses.*

// cata

inline fun <reified F, reified T, A> HK<T, F>.cata(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, A>
) = RT.cata(this, alg, FF)

inline fun <reified F, reified M, reified T, A> HK<T, F>.cataM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline alg: AlgebraM<M, F, A>
) = RT.cataM(this, alg, TF, MM)

inline fun <reified F, reified W, reified T, A> HK<T, F>.gcata(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        CW: Comonad<W> = comonad(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        noinline alg: GAlgebra<W, F, A>
) = RT.gcata(this, dFW, alg, FF, CW)

inline fun <reified F, reified W, reified M, reified T, A> HK<T, F>.gcataM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        TW: Traverse<W> = traverse(),
        MM: Monad<M> = monad(),
        CW: Comonad<W> = comonad(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        noinline alg: GAlgebraM<W, M, F, A>
) = RT.gcataM(this, dFW, alg, TF, TW, MM, CW)

// para

inline fun <reified F, reified T, A> HK<T, F>.para(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline alg: GAlgebra<PairKWKindPartial<HK<T, F>>, F, A>
) = RT.para(this, alg, FF)

inline fun <reified F, reified M, reified T, A> HK<T, F>.paraM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline alg: GAlgebraM<PairKWKindPartial<HK<T, F>>, M, F, A>
) = RT.paraM(this, alg, TF, MM)

// histo

inline fun <reified F, reified T, A> HK<T, F>.histo(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline alg: GAlgebra<CofreeKindPartial<F>, F, A>
) = RT.histo(this, alg, FF)

inline fun <reified F, reified W, reified T, A> HK<T, F>.ghisto(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        FW: Functor<W> = functor(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        noinline alg: GAlgebra<CofreeKindPartial<W>, F, A>
) = RT.ghisto(this, dFW, alg, FF, FW)

// ana

inline fun <reified F, reified T, A> A.ana(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        noinline coalg: Coalgebra<F, A>
) = CT.ana(this, coalg, FF)

inline fun <reified F, reified M, reified T, A> A.anaM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline coalg: CoalgebraM<M, F, A>
) = CT.anaM(this, coalg, TF, MM)

inline fun <reified F, reified N, reified T, A> A.gana(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        MN: Monad<N> = monad(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline coalg: GCoalgebra<N, F, A>
) = CT.gana(this, dNF, coalg, FF, MN)

inline fun <reified F, reified N, reified M, reified T, A> A.ganaM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MN: Monad<N> = monad(),
        TN: Traverse<N> = traverse(),
        MM: Monad<M> = monad(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline coalg: GCoalgebraM<N, M, F, A>
) = CT.ganaM(this, dNF, coalg, TF, MN, TN, MM)

// apo

inline fun <reified F, reified T, A> A.apo(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        noinline coalg: GCoalgebra<EitherKindPartial<HK<T, F>>, F, A>
) = CT.apo(this, coalg, FF)

inline fun <reified F, reified M, reified T,A> A.apoM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline coalg: GCoalgebraM<EitherKindPartial<HK<T, F>>, M, F, A>
) = CT.apoM(this, coalg, TF, MM)

// futu
inline fun <reified F, reified T, A> A.futu(
        CT: Corecursive<T> = corecursive(),
        MF: Monad<F> = monad(),
        noinline coalg: GCoalgebra<FreeKindPartial<F>, F, A>
) = CT.futu(this, coalg, MF)

inline fun <reified F, reified M, reified T, A> A.futuM(
        CT: Corecursive<T> = corecursive(),
        MF: Monad<F> = monad(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline coalg: GCoalgebraM<FreeKindPartial<F>, M, F, A>
) = CT.futuM(this, coalg, MF, TF, MM)

// hylo

inline fun <reified F, A, B> A.hylo(
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, B>,
        noinline coalg: Coalgebra<F, A>
) = hylo(this, alg, coalg, FF)

inline fun <reified F, reified M, A, B> A.hyloM(
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline alg: AlgebraM<M, F, B>,
        noinline coalg: CoalgebraM<M, F, A>
) = hyloM(this, alg, coalg, TF, MM)

inline fun <reified W, reified N, reified F, A, B> A.ghylo(
        CW: Comonad<W> = comonad(),
        MN: Monad<N> = monad(),
        FF: Functor<F> = functor(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline alg: GAlgebra<W, F, B>,
        noinline coalg: GCoalgebra<N, F, A>
) = ghylo(this, dFW, dNF, alg, coalg, CW, MN, FF)

inline fun <reified W, reified N, reified M, reified F, A, B> A.ghyloM(
        CW: Comonad<W> = comonad(),
        TW: Traverse<W> = traverse(),
        MN: Monad<N> = monad(),
        TN: Traverse<N> = traverse(),
        MM: Monad<M> = monad(),
        TF: Traverse<F> = traverse(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline alg: GAlgebraM<W, M, F, B>,
        noinline coalg: GCoalgebraM<N, M, F, A>
) = ghyloM(this, dFW, dNF, alg, coalg, CW, TW, MN, TN, MM, TF)

// other

inline fun <reified F, reified G> distributiveLaw() = distributiveLaw(traverse<F>(), applicative<G>())

inline fun <reified F, reified T> project(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor()
) = RT.project(FF)

inline fun <reified F, reified T> embed(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor()
) = CT.embed(FF)
