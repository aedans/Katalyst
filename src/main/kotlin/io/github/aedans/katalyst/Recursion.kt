package io.github.aedans.katalyst

import kategory.*

// TODO stack safe
fun <F, A, B> hylo(a: A, alg: Algebra<F, B>, coalg: Coalgebra<F, A>, FF: Functor<F>): B =
        alg(FF.map(coalg(a), { hylo(it, alg, coalg, FF) }))

fun <M, F, A, B> hyloM(a: A, algM: AlgebraM<M, F, B>, coalgM: CoalgebraM<M, F, A>, TF: Traverse<F>, MM: Monad<M>): HK<M, B> =
        hylo(
                a,
                { MM.flatMap(it.unnest()) { MM.flatMap(TF.sequence(MM, it), algM) } },
                { coalgM(it).nest() },
                ComposedFunctor(MM, TF)
        )

inline fun <reified F, A, B> A.hylo(
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, B>,
        noinline coalg: Coalgebra<F, A>
): B = hylo(this, alg, coalg, FF)

inline fun <reified M, reified F, A, B> A.hyloM(
        TF: Traverse<F> = traverse(),
        MM: Monad<M> = monad(),
        noinline algM: AlgebraM<M, F, B>,
        noinline coalgM: CoalgebraM<M, F, A>
): HK<M, B> = hyloM(this, algM, coalgM, TF, MM)
