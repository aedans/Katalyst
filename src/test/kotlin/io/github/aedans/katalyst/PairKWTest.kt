package io.github.aedans.katalyst

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq

class PairKWTest : UnitSpec() {
    init {
        testLaws(
                EqLaws.laws { PairKW.pure(it) },
                MonadLaws.laws(PairKW.monad(), Eq.any()),
                TraverseLaws.laws(PairKW.traverse(), PairKW.functor(), { PairKW.pure(it) }, Eq.any())
        )
    }
}
