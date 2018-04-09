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
        RecursiveLaws.laws(BT) + CorecursiveLaws.laws(BT) + listOf(
                Law("Birecursive Laws: ana . cata == hylo") {
                    forAll(intGen) {
                        val composed = it
                                .ana(toGNatCoalgebra(), Option.functor())
                                .cata(fromGNatAlgebra(), Option.functor())
                        val hylo = hylo(it, fromGNatAlgebra(), toGNatCoalgebra(), Option.functor())
                        hylo == composed
                    }
                },
                Law("Birecursive Laws: Stack-safe cata, ana, and hylo") {
                    100000.toGNat(BT).cata(fromGNatAlgebra(), Option.functor()) shouldEqual 100000
                }
        )
    }
}
