package io.github.aedans.katalyst

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.github.aedans.katalyst.data.*

class CoEnvTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws<CoEnvOf<Unit, ForId, Int>>(CoEnv.eq(), { CoEnv(Right(Id(it))) }),
                TraverseLaws.laws<CoEnvPartialOf<Unit, ForId>>(CoEnv.traverse(Id.traverse()), CoEnv.functor(Id.functor()), cf = { CoEnv(Right(Id(it))) }, EQ = Eq.any()),
                MonadLaws.laws<CoEnvPartialOf<Unit, ForId>>(CoEnv.monad(Id.monad(), Id.traverse()), Eq.any())
        )
    }
}
