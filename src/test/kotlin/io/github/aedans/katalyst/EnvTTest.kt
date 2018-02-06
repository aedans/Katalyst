package io.github.aedans.katalyst

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.github.aedans.katalyst.data.*

class EnvTTest : UnitSpec() {
    init {
        testLaws(
                TraverseLaws.laws(EnvT.traverse(), EnvT.functor(), cf = { EnvT(Unit toT Id(it)) }, EQ = Eq.any()),
                ComonadLaws.laws(EnvT.comonad(), cf = { EnvT(Unit toT Id(it)) }, EQ = Eq.any())
        )
    }
}
