package io.github.aedans.katalyst.laws

import arrow.core.OptionHK
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.*

object BirecursiveLaws {
    inline fun <reified T> laws(): List<Law> = RecursiveLaws.laws<T>() + CorecursiveLaws.laws<T>() + listOf(
            Law("Birecursive Laws: Nat Conversion") {
                forAll(intGen) {
                    it.toNatR<T>().toInt() == it
                }
            },
            Law("Birecursive Laws: List Conversion") {
                forAll(Gen.list(Gen.create { Unit })) {
                    it.toListR<T, Unit>().toList() == it
                }
            },
            Law("Birecursive Laws: ana . cata == hylo") {
                forAll(intGen) {
                    val composed = it.ana<OptionHK, T, Int>(coalg = toNatRCoalgebra()).cata(alg = fromNatRAlgebra())
                    val hylo = it.hylo(alg = fromNatRAlgebra(), coalg = toNatRCoalgebra())
                    hylo == composed
                }
            }
    )
}
