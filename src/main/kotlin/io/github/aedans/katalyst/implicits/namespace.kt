package io.github.aedans.katalyst.implicits

import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.typeclasses.*
import kategory.*

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
        noinline algM: AlgebraM<M, F, A>
) = RT.cataM(this, algM, TF, MM)

inline fun <reified F, reified W, reified T, A> HK<T, F>.gcata(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        CW: Comonad<W> = comonad(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        noinline gAlg: GAlgebra<W, F, A>
) = RT.gcata(this, dFW, gAlg, FF, CW)

inline fun <reified F, reified W, reified M, reified T, A> HK<T, F>.gcataM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        TW: Traverse<W> = traverse(),
        MM: Monad<M> = monad(),
        CW: Comonad<W> = comonad(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        noinline gAlgM: GAlgebraM<W, M, F, A>
) = RT.gcataM(this, dFW, gAlgM, TF, TW, MM, CW)

// para

inline fun <reified F, reified T, A> HK<T, F>.para(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor(),
        noinline gAlg: GAlgebra<PairKWKindPartial<HK<T, F>>, F, A>
) = RT.para(this, gAlg, FF)

inline fun <reified F, reified M, reified T, A> HK<T, F>.paraM(
        RT: Recursive<T> = recursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline gAlgM: GAlgebraM<PairKWKindPartial<HK<T, F>>, M, F, A>
) = RT.paraM(this, gAlgM, TF, MM)

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
        noinline coalgM: CoalgebraM<M, F, A>
) = CT.anaM(this, coalgM, TF, MM)

inline fun <reified F, reified N, reified T, A> A.gana(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        MN: Monad<N> = monad(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline gCoalg: GCoalgebra<N, F, A>
) = CT.gana(this, dNF, gCoalg, FF, MN)

inline fun <reified F, reified N, reified M, reified T, A> A.ganaM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MN: Monad<N> = monad(),
        TN: Traverse<N> = traverse(),
        MM: Monad<M> = monad(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline gCoalgM: GCoalgebraM<N, M, F, A>
) = CT.ganaM(this, dNF, gCoalgM, TF, MN, TN, MM)

// apo

inline fun <reified F, reified T, A> A.apo(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor(),
        noinline gCoalg: GCoalgebra<EitherKindPartial<HK<T, F>>, F, A>
) = CT.apo(this, gCoalg, FF)

inline fun <reified F, reified M, reified T,A> A.apoM(
        CT: Corecursive<T> = corecursive(),
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline gCoalgM: GCoalgebraM<EitherKindPartial<HK<T, F>>, M, F, A>
) = CT.apoM(this, gCoalgM, TF, MM)

// hylo

inline fun <reified F, A, B> A.hylo(
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, B>,
        noinline coalg: Coalgebra<F, A>
) = hylo(this, alg, coalg, FF)

inline fun <reified F, reified M, A, B> A.hyloM(
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline algM: AlgebraM<M, F, B>,
        noinline coalgM: CoalgebraM<M, F, A>
) = hyloM(this, algM, coalgM, TF, MM)

inline fun <reified W, reified N, reified F, A, B> A.ghylo(
        CW: Comonad<W> = comonad(),
        MN: Monad<N> = monad(),
        FF: Functor<F> = functor(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline gAlg: GAlgebra<W, F, B>,
        noinline gCoalg: GCoalgebra<N, F, A>
) = ghylo(this, dFW, dNF, gAlg, gCoalg, CW, MN, FF)

inline fun <reified W, reified N, reified M, reified F, A, B> A.ghyloM(
        CW: Comonad<W> = comonad(),
        TW: Traverse<W> = traverse(),
        MN: Monad<N> = monad(),
        TN: Traverse<N> = traverse(),
        MM: Monad<M> = monad(),
        TF: Traverse<F> = traverse(),
        dFW: DistributiveLaw<F, W> = distributiveLaw(),
        dNF: DistributiveLaw<N, F> = distributiveLaw(),
        noinline gAlgM: GAlgebraM<W, M, F, B>,
        noinline gCoalgM: GCoalgebraM<N, M, F, A>
) = ghyloM(this, dFW, dNF, gAlgM, gCoalgM, CW, TW, MN, TN, MM, TF)

// other

inline fun <reified F, reified G> distributiveLaw() = distributiveLaw(traverse<F>(), applicative<G>())

inline fun <reified F, reified T> HK<T, F>.project(
        RT: Recursive<T> = recursive(),
        FF: Functor<F> = functor()
) = RT.project(this, FF)

inline fun <reified F, reified T> HK<F, HK<T, F>>.embed(
        CT: Corecursive<T> = corecursive(),
        FF: Functor<F> = functor()
) = CT.embed(this, FF)
