package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.cata
import kategory.HK
import kategory.Option.None
import kategory.Option.Some
import kategory.OptionHK
import kategory.ev

typealias Nat<T> = HK<T, OptionHK>

val toNat = Coalgebra<OptionHK, Int> {
    if (it == 0) None else Some(it - 1)
}

val fromNat = Algebra<OptionHK, Int> {
    it.ev().fold({ 0 }, { it + 1 })
}

inline fun <reified T> factorial() = GAlgebra<PairKWKindPartial<Nat<T>>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 1 }, { (i, n) -> (i.int() + 1) * n })
}

inline fun <reified T> Int.nat(): Nat<T> = ana(coalg = toNat)
inline fun <reified T> Nat<T>.int() = cata(alg = fromNat)
