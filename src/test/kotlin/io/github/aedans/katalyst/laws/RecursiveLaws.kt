package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.nat
import io.github.aedans.katalyst.implicits.cata
import io.github.aedans.katalyst.implicits.cataM
import io.github.aedans.katalyst.implicits.gcata
import io.github.aedans.katalyst.implicits.gcataM
import io.kotlintest.properties.forAll
import io.kotlintest.properties.map
import kategory.*

object RecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Recursive Laws: cata == cataM Id") {
                forAll(intGen.map { it.nat<T>() }) {
                    val cata: Int = it.cata { it.ev().fold({ 0 }, { it + 1 }) }
                    val cataM: IdKind<Int> = it.cataM { Id.pure(it.ev().fold({ 0 }, { it + 1 })) }
                    cata == cataM.value()
                }
            },
            Law("Recursive Laws: cata == gcata Id") {
                forAll(intGen.map { it.nat<T>() }) {
                    val cata: Int = it.cata { it.ev().fold({ 0 }, { it + 1 }) }
                    val gcata: Int = it.gcata<OptionHK, IdHK, T, Int> { it.ev().fold({ 0 }, { it.value() + 1 }) }
                    cata == gcata
                }
            },
            Law("Recursive Laws: cata == gcataM Id Id") {
                forAll(intGen.map { it.nat<T>() }) {
                    val cata: Int = it.cata { it.ev().fold({ 0 }, { it + 1 }) }
                    val gcata: IdKind<Int> = it.gcataM<OptionHK, IdHK, IdHK, T, Int> { Id.pure(it.ev().fold({ 0 }, { it.value() + 1 })) }
                    cata == gcata.value()
                }
            }
    )
}
