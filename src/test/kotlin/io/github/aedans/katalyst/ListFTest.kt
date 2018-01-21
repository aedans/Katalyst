package io.github.aedans.katalyst

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.fixedpoint.*

class ListFTest : UnitSpec() {
    init {
        testLaws(EqLaws.laws { ListF.pure(it) })
        testLaws(FunctorLaws.laws<ListFKindPartial<*>>(functor(), { ListF.pure(it) }, eq()))
        testLaws(ApplicativeLaws.laws<ListFKindPartial<*>>(applicative(), eq()))
        testLaws(MonadLaws.laws<ListFKindPartial<*>>(monad(), eq()))
        testLaws(FoldableLaws.laws<ListFKindPartial<*>>(foldable(), { ListF.pure(it) }, eq()))
        testLaws(TraverseLaws.laws<ListFKindPartial<*>>(traverse(), functor(), { ListF.pure(it) }))
    }
}
