package io.github.aedans.katalyst

import io.github.aedans.katalyst.fixedpoint.ListF
import io.github.aedans.katalyst.fixedpoint.ListFKindPartial
import kategory.*
import kategory.laws.EqLaws

class ListTest : UnitSpec() {
    init {
        testLaws(EqLaws.laws { ListF.pure(it) })
        testLaws(FunctorLaws.laws<ListFKindPartial<*>>(applicative(), eq()))
        testLaws(ApplicativeLaws.laws<ListFKindPartial<*>>(applicative(), eq()))
        testLaws(MonadLaws.laws<ListFKindPartial<*>>(monad(), eq()))
        testLaws(TraverseLaws.laws<ListFKindPartial<*>>(traverse(), functor(), { ListF.pure(it) }))
    }
}
