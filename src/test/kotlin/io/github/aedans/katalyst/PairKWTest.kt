package io.github.aedans.katalyst

import kategory.*

class PairKWTest : UnitSpec() {
    init {
        testLaws(FunctorLaws.laws(PairKW.functor(), { Unit toKW it }, Eq.any()))
        testLaws(FoldableLaws.laws(PairKW.foldable(), { Unit toKW it }, Eq.any()))
        testLaws(TraverseLaws.laws(PairKW.traverse(), PairKW.functor(), { Unit toKW it }, Eq.any()))
    }
}
