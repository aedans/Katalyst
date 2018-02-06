package io.github.aedans.katalyst

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.fixedpoint.*

class AndMaybeTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws { AndMaybe.pure(it) },
                MonadLaws.laws<AndMaybeKindPartial<*>>(monad(), eq()),
                TraverseLaws.laws<AndMaybeKindPartial<*>>(traverse(), functor(), { AndMaybe.pure(it) })
        )
    }
}
