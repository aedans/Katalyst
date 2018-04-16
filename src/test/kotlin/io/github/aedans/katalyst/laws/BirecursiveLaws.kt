package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.hylo
import io.github.aedans.katalyst.typeclasses.Birecursive
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.properties.forAll

object BirecursiveLaws {
    inline fun <reified T> laws(BT: Birecursive<T>): List<Law> = BT.run {
        CorecursiveLaws.laws(BT) + RecursiveLaws.laws(BT) + listOf(
                Law("Birecursive Laws: ana . cata == hylo") {
                    forAll(intGen) {
                        val composed = it
                                .ana(Option.functor(), toGNatCoalgebra())
                                .cata(Option.functor(), fromGNatAlgebra())
                        val hylo = hylo(Option.functor(), fromGNatAlgebra(), toGNatCoalgebra(), it)
                        hylo == composed
                    }
                },
                Law("Birecursive Laws: Stack-safe cata, ana, and hylo") {
                    100000.toGNat(BT).cata(Option.functor(), fromGNatAlgebra()) shouldEqual 100000
                }
        )
    }
}
