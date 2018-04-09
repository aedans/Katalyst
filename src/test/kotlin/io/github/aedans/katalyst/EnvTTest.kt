package io.github.aedans.katalyst

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.github.aedans.katalyst.data.*

class EnvTTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws(EnvT.eq(), { EnvT(Unit toT Id(it)) }),
                TraverseLaws.laws(EnvT.traverse<Unit, ForId>(Id.traverse()), EnvT.functor<Unit, ForId>(Id.functor()), cf = { EnvT(Unit toT Id(it)) }, EQ = Eq.any()),
                ComonadLaws.laws(EnvT.comonad<Unit, ForId>(Id.comonad()), cf = { EnvT(Unit toT Id(it)) }, EQ = Eq.any())
        )
    }
}
