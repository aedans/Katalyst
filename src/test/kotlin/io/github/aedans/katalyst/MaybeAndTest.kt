package io.github.aedans.katalyst

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.data.*

class MaybeAndTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws { MaybeAnd.pure(it) },
                MonadLaws.laws<MaybeAndKindPartial<*>>(monad(), eq()),
                TraverseLaws.laws<MaybeAndKindPartial<*>>(traverse(), functor(), { MaybeAnd.pure(it) })
        )
    }
}
