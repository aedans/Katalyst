package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.natR
import io.kotlintest.properties.Gen
import io.kotlintest.properties.map

val intGen = Gen.Companion.choose(0, 10)

inline fun <reified T> natGen() = intGen.map { it.natR<T>() }
