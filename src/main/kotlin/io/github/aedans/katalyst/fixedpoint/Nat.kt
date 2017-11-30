package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.implicits.*
import kategory.*
import kategory.Option.*

typealias NatR<T> = HK<T, OptionHK>

fun Algebras.toNatR() = Coalgebra<OptionHK, Int> {
    if (it == 0) None else Some(it - 1)
}

fun Algebras.fromNatR() = Algebra<OptionHK, Int> {
    it.ev().fold({ 0 }, { it + 1 })
}

inline fun <reified T> Algebras.factorial() = GAlgebra<PairKWKindPartial<NatR<T>>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 1 }, { (i, n) -> (i.int() + 1) * n })
}

inline fun <reified T> Int.natR(): NatR<T> = ana(coalg = Algebras.toNatR())
inline fun <reified T> NatR<T>.int() = cata(alg = Algebras.fromNatR())
