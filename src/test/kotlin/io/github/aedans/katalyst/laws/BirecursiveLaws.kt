package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.Algebras
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
