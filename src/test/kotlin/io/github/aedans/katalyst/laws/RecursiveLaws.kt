package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.fromGNatAlgebra
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.forAll

object RecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Recursive Laws: cata == cataM Id") {
                forAll(gNatGen<T>()) {
                    val cata: Int = it.cata(alg = fromGNatAlgebra())
                    val cataM: IdKind<Int> = it.cataM { Id.pure(fromGNatAlgebra()(it)) }
                    cata == cataM.value()
                }
            },
            Law("Recursive Laws: cata == gcata Id") {
                forAll(gNatGen<T>()) {
                    val cata: Int = it.cata(alg = fromGNatAlgebra())
                    val gcata: Int = it.gcata<OptionHK, IdHK, T, Int> { it.ev().fold({ 0 }, { it.value() + 1 }) }
                    cata == gcata
                }
            },
            Law("Recursive Laws: cata == gcataM Id Id") {
                forAll(gNatGen<T>()) {
                    val cata: Int = it.cata(alg = fromGNatAlgebra())
                    val gcata: IdKind<Int> = it.gcataM<OptionHK, IdHK, IdHK, T, Int> { Id.pure(it.ev().fold({ 0 }, { it.value() + 1 })) }
                    cata == gcata.value()
                }
            },
            Law("Recursive Laws: para == paraM Id") {
                forAll(gNatGen<T>()) {
                    val para: Int = it.para(alg = paraFactorialAlgebra())
                    val paraM: IdKind<Int> = it.paraM { Id.pure(paraFactorialAlgebra<T>()(it)) }
                    para == paraM.value()
                }
            },
            Law("Recursive Laws: histo == ghisto Id") {
                forAll(gNatGen<T>()) {
                    val histo: Int = it.histo(alg = histoFromGNatAlgebra())
                    val histoM: Int = it.ghisto(alg = histoFromGNatAlgebra())
                    histo == histoM
                }
            }
    )
}
