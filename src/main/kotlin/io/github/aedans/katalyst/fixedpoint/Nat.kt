package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import arrow.core.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.syntax.*

/**
 * Recursive numbers parameterized by a recursive type combinator.
 */
typealias NatR<T> = HK<T, OptionHK>

fun toNatRCoalgebra() = Coalgebra<OptionHK, Int> {
    if (it == 0) None else Some(it - 1)
}

fun fromNatRAlgebra() = Algebra<OptionHK, Int> {
    it.ev().fold({ 0 }, { it + 1 })
}

inline fun <reified T> factorialAlgebra() = GAlgebra<PairKWKindPartial<NatR<T>>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 1 }, { (i, n) -> (i.toInt() + 1) * n })
}

inline fun <reified T> Int.toNatR(): NatR<T> = ana(coalg = toNatRCoalgebra())
inline fun <reified T> NatR<T>.toInt() = cata(alg = fromNatRAlgebra())
