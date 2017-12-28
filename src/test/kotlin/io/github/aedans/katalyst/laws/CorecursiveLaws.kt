package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.properties.forAll
import kategory.*

object CorecursiveLaws {
    inline fun <reified T> laws(): List<Law> = listOf(
            Law("Corecursive Laws: ana == anaM Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = Algebras.toNatR())
                    val anaM: IdKind<NatR<T>> = it.anaM { Id.pure(Algebras.toNatR()(it)) }
                    ana.int() == anaM.ev().value.int()
                }
            },
            Law("Corecursive Laws: ana == gana Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = Algebras.toNatR())
                    val gana: NatR<T> = it.gana { if (it == 0) Option.None else Option.Some(Id.pure(it - 1)) }
                    ana.int() == gana.int()
                }
            },
            Law("Corecursive Laws: ana == ganaM Id Id") {
                forAll(intGen) {
                    val ana: NatR<T> = it.ana(coalg = Algebras.toNatR())
                    val gana: IdKind<NatR<T>> = it.ganaM { Id.pure(if (it == 0) Option.None else Option.Some(Id.pure(it - 1))) }
                    ana.int() == gana.value().int()
                }
            },
            Law("Corecursive Laws: apo == apoM Id") {
                forAll(intGen) {
                    val gCoalg: GCoalgebra<EitherKindPartial<NatR<T>>, OptionHK, Int> = {
                        when {
                            it == 0 -> Option.None
                            it % 2 == 0 -> Option.Some(1.natR<T>().left())
                            else -> Option.Some((it - 1).right())
                        }
                    }

                    val apo: NatR<T> = it.apo(gCoalg = gCoalg)
                    val apoM: IdKind<NatR<T>> = it.apoM { Id.pure(gCoalg(it)) }
                    apo.int() == apoM.value().int()
                }
            }
    )
}
