package io.github.aedans.katalyst

import arrow.core.Eval
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.syntax.*

/**
 * The composition of cata and ana.
 */
fun <F, A, B> hylo(
        a: A,
        alg: Algebra<F, Eval<B>>,
        coalg: Coalgebra<F, A>,
        FF: Functor<F>
): B {
    fun h(a: A): Eval<B> = alg(FF.map(coalg(a)) { Eval.defer { h(it) } })
    return h(a).value()
}
