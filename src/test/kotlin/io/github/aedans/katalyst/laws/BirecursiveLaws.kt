package io.github.aedans.katalyst.laws

import arrow.core.OptionHK
import arrow.test.laws.Law
import io.github.aedans.katalyst.Algebras
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.*

object BirecursiveLaws {
    inline fun <reified T> laws(): List<Law> = RecursiveLaws.laws<T>() + CorecursiveLaws.laws<T>() + listOf(
            Law("Birecursive Laws: Nat Conversion") {
                forAll(intGen) {
                    it.natR<T>().int() == it
                }
            },
            Law("Birecursive Laws: List Conversion") {
                forAll(Gen.list(Gen.create { Unit })) {
                    it.listR<T, Unit>().list() == it
                }
            },
            Law("Birecursive Laws: ana . cata == hylo") {
                forAll(intGen) {
                    val composed = it.ana<OptionHK, T, Int>(coalg = Algebras.toNatR()).cata(alg = Algebras.fromNatR())
                    val hylo = it.hylo(alg = Algebras.fromNatR(), coalg = Algebras.toNatR())
                    hylo == composed
                }
            }
    )
}
