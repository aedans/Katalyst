package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.free.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.*

val intGen = Gen.choose(0, 1000)

inline fun <reified T> gNatGen() = intGen.map { it.toGNat<T>() }

fun <T> paraFromGNatAlgebra() = GAlgebra<PairKWKindPartial<GNat<T>>, NatPattern, Int> {
    it.ev().map { it.ev() }.fold({ 0 }, { it.b + 1 })
}

fun histoFromGNatAlgebra() = GAlgebra<CofreeKindPartial<NatPattern>, NatPattern, Int> {
    it.ev().map { it.ev() }.fold({ 0 }, { it.head + 1 })
}

fun <T> apoToGNatCoalgebra() = GCoalgebra<EitherKindPartial<GNat<T>>, NatPattern, Int> {
    if (it == 0) Option.empty() else Option.pure(Right(it - 1))
}

fun futuToGNatCoalgebra() = GCoalgebra<FreeKindPartial<NatPattern>, NatPattern, Int> { it ->
    if (it == 0) Option.empty() else Option.pure(Free.pure(it - 1))
}
