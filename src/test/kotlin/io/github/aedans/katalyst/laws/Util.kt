package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.free.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.GAlgebra
import io.kotlintest.properties.*

val intGen = Gen.Companion.choose(0, 10)

inline fun <reified T> gNatGen() = intGen.map { it.toGNat<T>() }

inline fun <reified T> paraFactorialAlgebra() = GAlgebra<PairKWKindPartial<GNat<T>>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 1 }, { (i, n) -> (i.toInt() + 1) * n })
}

fun histoFromGNatAlgebra() = GAlgebra<CofreeKindPartial<OptionHK>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 0 }, { it.head + 1 })
}
