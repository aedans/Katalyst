package io.github.aedans.katalyst

import arrow.test.UnitSpec
import io.github.aedans.katalyst.data.FixHK
import io.github.aedans.katalyst.laws.BirecursiveLaws

class FixTest : UnitSpec() {
    init {
        testLaws(
                BirecursiveLaws.laws<FixHK>()
        )
    }
}
