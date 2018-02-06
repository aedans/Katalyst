package io.github.aedans.katalyst

import arrow.core.Id
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq

class EnvTTest : UnitSpec() {
    init {
        testLaws(
                TraverseLaws.laws(cf = { Id(it) }),
                ComonadLaws.laws(cf = { Id(it) }, EQ = Eq.any())
        )
    }
}
