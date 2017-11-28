package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.int
import io.github.aedans.katalyst.fixedpoint.list
import io.github.aedans.katalyst.fixedpoint.nat
import io.github.aedans.katalyst.fixedpoint.rList
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Law

object BirecursiveLaws {
    inline fun <reified T> laws(): List<Law> = RecursiveLaws.laws<T>() + CorecursiveLaws.laws<T>() + listOf(
            Law("Birecursive Laws: Nat Conversion") {
                forAll(intGen) {
                    it.nat<T>().int == it
                }
            },
            Law("Birecursive Laws: List Conversion") {
                forAll(Gen.list(Gen.create { Unit })) {
                    it.rList<T, Unit>().list == it
                }
            }
    )
}
