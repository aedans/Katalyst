package io.github.aedans.katalyst.laws

import io.kotlintest.properties.Gen

val intGen = Gen.Companion.choose(0, 100)
