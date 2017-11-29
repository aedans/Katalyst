package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.cata
import io.github.aedans.katalyst.implicits.hylo
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Law
import kategory.OptionHK

object BirecursiveLaws {
    inline fun <reified T> laws(): List<Law> = RecursiveLaws.laws<T>() + CorecursiveLaws.laws<T>() + listOf(
            Law("Birecursive Laws: Nat Conversion") {
                forAll(intGen) {
                    it.nat<T>().int() == it
                }
            },
            Law("Birecursive Laws: List Conversion") {
                forAll(Gen.list(Gen.create { Unit })) {
                    it.rList<T, Unit>().list() == it
                }
            },
            Law("Birecursive Laws: ana . cata == hylo") {
                forAll(intGen) {
                    val composed = it.ana<OptionHK, T, Int>(coalg = toNat).cata(alg = fromNat)
                    val hylo = it.hylo(alg = fromNat, coalg = toNat)
                    hylo == composed
                }
            }
    )
}
