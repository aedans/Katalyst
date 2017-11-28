package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.NuHK
import io.github.aedans.katalyst.laws.BirecursiveLaws
import kategory.UnitSpec

class NuTest : UnitSpec() {
    init {
        testLaws(BirecursiveLaws.laws<NuHK>())
    }
}
