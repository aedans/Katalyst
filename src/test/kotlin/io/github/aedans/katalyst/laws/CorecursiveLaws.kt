package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.fixedpoint.Nat
import io.github.aedans.katalyst.fixedpoint.int
import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.anaM
import io.github.aedans.katalyst.implicits.gana
import io.github.aedans.katalyst.implicits.ganaM
import io.kotlintest.properties.forAll
import kategory.*

object CorecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Corecursive Laws: ana == anaM Id") {
                forAll(intGen) {
                    val ana: Nat<T> = it.ana { if (it == 0) Option.None else Option.Some(it - 1) }
                    val anaM: IdKind<Nat<T>> = it.anaM { Id.pure(if (it == 0) Option.None else Option.Some(it - 1)) }
                    ana.int == anaM.ev().value.int
                }
            },
            Law("Corecursive Laws: ana == gana Id") {
                forAll(intGen) {
                    val ana: Nat<T> = it.ana { if (it == 0) Option.None else Option.Some(it - 1) }
                    val gana: Nat<T> = it.gana { if (it == 0) Option.None else Option.Some(Id.pure(it - 1)) }
                    ana.int == gana.int
                }
            },
            Law("Corecursive Laws: ana == ganaM Id Id") {
                forAll(intGen) {
                    val ana: Nat<T> = it.ana { if (it == 0) Option.None else Option.Some(it - 1) }
                    val gana: IdKind<Nat<T>> = it.ganaM { Id.pure(if (it == 0) Option.None else Option.Some(Id.pure(it - 1))) }
                    ana.int == gana.value().int
                }
            }
    )
}
