package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.toNatR
import io.kotlintest.properties.*

val intGen = Gen.Companion.choose(0, 10)

inline fun <reified T> natGen() = intGen.map { it.toNatR<T>() }
