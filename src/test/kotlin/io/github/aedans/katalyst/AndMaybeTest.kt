package io.github.aedans.katalyst

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.fixedpoint.*

class AndMaybeTest : UnitSpec() {
    init {
        testLaws(EqLaws.laws { AndMaybe.pure(it) })
        testLaws(FunctorLaws.laws<AndMaybeKindPartial<*>>(functor(), { AndMaybe.pure(it) }, eq()))
        testLaws(ApplicativeLaws.laws<AndMaybeKindPartial<*>>(applicative(), eq()))
        testLaws(MonadLaws.laws<AndMaybeKindPartial<*>>(monad(), eq()))
        testLaws(FoldableLaws.laws<AndMaybeKindPartial<*>>(foldable(), { AndMaybe.pure(it) }, eq()))
        testLaws(TraverseLaws.laws<AndMaybeKindPartial<*>>(traverse(), functor(), { AndMaybe.pure(it) }))
    }
}
