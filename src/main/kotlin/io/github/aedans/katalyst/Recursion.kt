package io.github.aedans.katalyst

import kategory.*

// TODO stack safe
fun <F, A, B> hylo(
        a: A,
        alg: Algebra<F, B>,
        coalg: Coalgebra<F, A>,
        FF: Functor<F>
): B = alg(FF.map(coalg(a), { hylo(it, alg, coalg, FF) }))

fun <M, F, A, B> hyloM(
        a: A,
        algM: AlgebraM<M, F, B>,
        coalgM: CoalgebraM<M, F, A>,
        TF: Traverse<F>,
        MM: Monad<M>
): HK<M, B> = hylo(
        a,
        { MM.flatMap(it.unnest()) { MM.flatMap(TF.sequence(MM, it), algM) } },
        { coalgM(it).nest() },
        ComposedFunctor(MM, TF)
)

fun <W, N, F, A, B> ghylo(
        a: A,
        dFW: DistributiveLaw<F, W>,
        dNF: DistributiveLaw<N, F>,
        gAlg: GAlgebra<W, F, B>,
        gCoalg: GCoalgebra<N, F, A>,
        CW: Comonad<W>,
        MN: Monad<N>,
        FF: Functor<F>
): B = hylo<YonedaKindPartial<F>, HK<N, A>, HK<W, B>>(
        MN.pure(a),
        { CW.map(dFW.invoke(it.ev().map(CW::duplicate, FF).lower()), gAlg) },
        { Yoneda(FF.map(dNF.invoke(MN.map(it, gCoalg)), MN::flatten), FF) },
        Yoneda.functor(FF, Unit)
).let(CW::extract)

fun <W, N, M, F, A, B> ghyloM(
        a: A,
        dFW: DistributiveLaw<F, W>,
        dNF: DistributiveLaw<N, F>,
        gAlgM: GAlgebraM<W, M, F, B>,
        gCoalgM: GCoalgebraM<N, M, F, A>,
        CW: Comonad<W>,
        TW: Traverse<W>,
        MN: Monad<N>,
        TN: Traverse<N>,
        MM: Monad<M>,
        TF: Traverse<F>
): HK<M, B> = hyloM<M, F, HK<N, A>, HK<W, B>>(
        MN.pure(a),
        { TW.traverse(dFW.invoke(TF.map(it, CW::duplicate)), gAlgM, MM) },
        { MM.map(TN.traverse(it, gCoalgM, MM)) { TF.map(dNF.invoke(it), MN::flatten) } },
        TF,
        MM
).let { MM.map(it, CW::extract) }

fun <F> distCata(TF: Traverse<F>): DistributiveLaw<F, IdHK> = object : FunctionK<Nested<F, IdHK>, Nested<IdHK, F>> {
    override fun <A> invoke(fa: HK<Nested<F, IdHK>, A>) = TF.sequence(fa.unnest()).nest()
}
