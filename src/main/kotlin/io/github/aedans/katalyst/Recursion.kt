package io.github.aedans.katalyst

import kategory.*

fun <F, A, B> hylo(a: A, alg: Algebra<F, B>, coalg: Coalgebra<F, A>, FF: Functor<F>): B =
        alg(FF.map(coalg(a), { hylo(it, alg, coalg, FF) }))

inline fun <reified F, A, B> A.hylo(
        FF: Functor<F> = functor(),
        noinline alg: Algebra<F, B>,
        noinline coalg: Coalgebra<F, A>
) = hylo(this, alg, coalg, FF)
