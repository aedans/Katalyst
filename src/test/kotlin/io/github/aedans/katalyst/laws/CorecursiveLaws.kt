package io.github.aedans.katalyst.laws

import arrow.core.*
import arrow.syntax.either.*
import arrow.test.laws.Law
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.forAll

object CorecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Corecursive Laws: ana == anaM Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = toNatRCoalgebra())
                    val anaM: IdKind<NatR<T>> = it.anaM { Id.pure(toNatRCoalgebra()(it)) }
                    ana.toInt() == anaM.ev().value.toInt()
                }
            },
            Law("Corecursive Laws: ana == gana Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = toNatRCoalgebra())
                    val gana: NatR<T> = it.gana { if (it == 0) None else Some(Id.pure(it - 1)) }
                    ana.toInt() == gana.toInt()
                }
            },
            Law("Corecursive Laws: ana == ganaM Id Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = toNatRCoalgebra())
                    val gana: IdKind<NatR<T>> = it.ganaM { Id.pure(if (it == 0) None else Some(Id.pure(it - 1))) }
                    ana.toInt() == gana.value().toInt()
                }
            },
            Law("Corecursive Laws: apo == apoM Id") {
                forAll(intGen) {
                    val gCoalg: GCoalgebra<EitherKindPartial<NatR<T>>, OptionHK, Int> = {
                        when {
                            it == 0 -> None
                            it % 2 == 0 -> Some(1.toNatR<T>().left())
                            else -> Some((it - 1).right())
                        }
                    }

                    val apo: NatR<T> = it.apo(gCoalg = gCoalg)
                    val apoM: IdKind<NatR<T>> = it.apoM { Id.pure(gCoalg(it)) }
                    apo.toInt() == apoM.value().toInt()
                }
            }
    )
}
