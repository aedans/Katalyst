package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.toGNat
import io.github.aedans.katalyst.typeclasses.Corecursive
import io.kotlintest.properties.*

val intGen = Gen.choose(0, 1000)

inline fun <reified T> gNatGen(CT: Corecursive<T>) = intGen.map { it.toGNat(CT) }
