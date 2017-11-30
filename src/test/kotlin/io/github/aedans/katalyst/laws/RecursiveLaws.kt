package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.Algebras
import io.github.aedans.katalyst.fixedpoint.factorial
import io.github.aedans.katalyst.fixedpoint.fromNatR
import io.github.aedans.katalyst.implicits.*
import io.kotlintest.properties.forAll
import kategory.*

object RecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Recursive Laws: cata == cataM Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = Algebras.fromNatR())
                    val cataM: IdKind<Int> = it.cataM { Id.pure(Algebras.fromNatR()(it)) }
                    cata == cataM.value()
                }
            },
            Law("Recursive Laws: cata == gcata Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = Algebras.fromNatR())
                    val gcata: Int = it.gcata<OptionHK, IdHK, T, Int> { it.ev().fold({ 0 }, { it.value() + 1 }) }
                    cata == gcata
                }
            },
            Law("Recursive Laws: cata == gcataM Id Id") {
                forAll(natGen<T>()) {
                    val cata: Int = it.cata(alg = Algebras.fromNatR())
                    val gcata: IdKind<Int> = it.gcataM<OptionHK, IdHK, IdHK, T, Int> { Id.pure(it.ev().fold({ 0 }, { it.value() + 1 })) }
                    cata == gcata.value()
                }
            },
            Law("Recursive Laws: para == paraM Id") {
                forAll(natGen<T>()) {
                    val para: Int = it.para(gAlg = Algebras.factorial())
                    val paraM: IdKind<Int> = it.paraM { Id.pure(Algebras.factorial<T>()(it)) }
                    para == paraM.value()
                }
            }
    )
}
