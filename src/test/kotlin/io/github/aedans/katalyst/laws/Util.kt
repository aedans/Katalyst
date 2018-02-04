package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.free.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.GAlgebra
import io.kotlintest.properties.*

val intGen = Gen.Companion.choose(0, 10)

inline fun <reified T> natGen() = intGen.map { it.toNatR<T>() }

inline fun <reified T> paraFactorialAlgebra() = GAlgebra<PairKWKindPartial<NatR<T>>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 1 }, { (i, n) -> (i.toInt() + 1) * n })
}

fun histoFromNatRAlgebra() = GAlgebra<CofreeKindPartial<OptionHK>, OptionHK, Int> {
    it.ev().map { it.ev() }.fold({ 0 }, { it.head + 1 })
}
