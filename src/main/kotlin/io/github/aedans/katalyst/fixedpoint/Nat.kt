package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import arrow.core.*
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*

typealias NatPattern = OptionHK

/**
 * Natural numbers parameterized by a recursive type combinator.
 */
typealias GNat<T> = HK<T, NatPattern>

typealias FixNat = GNat<FixHK>
typealias MuNat = GNat<MuHK>
typealias NuNat = GNat<NuHK>

fun toGNatCoalgebra() = Coalgebra<NatPattern, Int> {
    if (it == 0) None else Some(it - 1)
}

fun fromGNatAlgebra() = Algebra<NatPattern, Eval<Int>> {
    it.ev().fold({ Eval.Zero }, { it.map { it + 1 } })
}

inline fun <reified T> Int.toGNat(): GNat<T> = ana(coalg = toGNatCoalgebra())
inline fun <reified T> GNat<T>.toInt(): Int = cata(alg = fromGNatAlgebra())
