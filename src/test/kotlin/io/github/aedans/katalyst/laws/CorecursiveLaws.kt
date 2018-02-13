package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.forAll

object CorecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Corecursive Laws: ana == anaM Id") {
                forAll(intGen) {
                    val ana: GNat<T> = it.ana(coalg = toGNatCoalgebra())
                    val anaM: IdKind<GNat<T>> = it.anaM { Id.pure(toGNatCoalgebra()(it)) }
                    ana.toInt() == anaM.ev().value.toInt()
                }
            },
            Law("Corecursive Laws: ana == gana Id") {
                forAll(intGen) {
                    val ana: GNat<T> = it.ana(coalg = toGNatCoalgebra())
                    val gana: GNat<T> = it.gana { if (it == 0) None else Some(Id.pure(it - 1)) }
                    ana.toInt() == gana.toInt()
                }
            },
            Law("Corecursive Laws: ana == ganaM Id Id") {
                forAll(intGen) {
                    val ana: GNat<T> = it.ana(coalg = toGNatCoalgebra())
                    val gana: IdKind<GNat<T>> = it.ganaM { Id.pure(if (it == 0) None else Some(Id.pure(it - 1))) }
                    ana.toInt() == gana.value().toInt()
                }
            },
            Law("Corecursive Laws: apo == apoM Id") {
                forAll(intGen) {
                    val apo: GNat<T> = it.apo(coalg = apoToGNatCoalgebra())
                    val apoM: IdKind<GNat<T>> = it.apoM { Id.pure(apoToGNatCoalgebra<T>()(it)) }
                    apo.toInt() == apoM.value().toInt()
                }
            },
            Law("Corecursive Laws: futu == futuM Id") {
                forAll(intGen) {
                    val futu: GNat<T> = it.futu(coalg = futuToGNatCoalgebra())
                    val futuM: IdKind<GNat<T>> = it.futuM { Id.pure(futuToGNatCoalgebra()(it)) }
                    futu.toInt() == futuM.value().toInt()
                }
            }
    )
}
