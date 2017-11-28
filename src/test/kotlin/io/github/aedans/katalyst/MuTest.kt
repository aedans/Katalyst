package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.MuHK
import io.github.aedans.katalyst.laws.BirecursiveLaws
import kategory.UnitSpec

class MuTest : UnitSpec() {
    init {
        testLaws(BirecursiveLaws.laws<MuHK>())
    }
}
