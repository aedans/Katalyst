package io.github.aedans.katalyst

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.github.aedans.katalyst.data.*

class CoEnvTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws<CoEnvKind<Unit, IdHK, Int>>(CoEnv.eq(), { CoEnv(Right(Id(it))) }),
                TraverseLaws.laws<CoEnvKindPartial<Unit, IdHK>>(CoEnv.traverse(), CoEnv.functor(), cf = { CoEnv(Right(Id(it))) }, EQ = Eq.any()),
                MonadLaws.laws<CoEnvKindPartial<Unit, IdHK>>(CoEnv.monad(), Eq.any())
        )
    }
}
