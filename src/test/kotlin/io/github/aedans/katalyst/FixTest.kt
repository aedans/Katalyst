package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.FixHK
import io.github.aedans.katalyst.laws.BirecursiveLaws
import kategory.UnitSpec

class FixTest : UnitSpec() {
    init {
        testLaws(BirecursiveLaws.laws<FixHK>())
    }
}
