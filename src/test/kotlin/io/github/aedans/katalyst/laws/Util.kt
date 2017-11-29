package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.nat
import io.kotlintest.properties.Gen
import io.kotlintest.properties.map

val intGen = Gen.Companion.choose(0, 100)

inline fun <reified T> natGen() = intGen.map { it.nat<T>() }
