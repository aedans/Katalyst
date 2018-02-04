package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.forAll

object RecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Recursive Laws: cata == cataM Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = fromNatRAlgebra())
                    val cataM: IdKind<Int> = it.cataM { Id.pure(fromNatRAlgebra()(it)) }
                    cata == cataM.value()
                }
            },
            Law("Recursive Laws: cata == gcata Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = fromNatRAlgebra())
                    val gcata: Int = it.gcata<OptionHK, IdHK, T, Int> { it.ev().fold({ 0 }, { it.value() + 1 }) }
                    cata == gcata
                }
            },
            Law("Recursive Laws: cata == gcataM Id Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = fromNatRAlgebra())
                    val gcata: IdKind<Int> = it.gcataM<OptionHK, IdHK, IdHK, T, Int> { Id.pure(it.ev().fold({ 0 }, { it.value() + 1 })) }
                    cata == gcata.value()
                }
            },
            Law("Recursive Laws: para == paraM Id") {
                forAll(natGen<T>()) {
                    val para: Int = it.para(gAlg = factorialAlgebra())
                    val paraM: IdKind<Int> = it.paraM { Id.pure(factorialAlgebra<T>()(it)) }
                    para == paraM.value()
                }
            }
    )
}
