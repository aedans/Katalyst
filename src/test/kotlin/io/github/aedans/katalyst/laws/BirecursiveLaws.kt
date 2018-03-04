package io.github.aedans.katalyst.laws

import arrow.core.OptionHK
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.properties.forAll

object BirecursiveLaws {
    inline fun <reified T> laws(): List<Law> = RecursiveLaws.laws<T>() + CorecursiveLaws.laws<T>() + listOf(
            Law("Birecursive Laws: ana . cata == hylo") {
                forAll(intGen) {
                    val composed = it.ana<OptionHK, T, Int>(coalg = toGNatCoalgebra()).cata(alg = fromGNatAlgebra())
                    val hylo = it.hylo(alg = fromGNatAlgebra(), coalg = toGNatCoalgebra())
                    hylo == composed
                }
            },
            Law("Birecursive Laws: Stack-safe cata, ana, and hylo") {
                10000.toGNat<T>().cata(alg = fromGNatAlgebra()) shouldEqual 100000
            }
    )
}
