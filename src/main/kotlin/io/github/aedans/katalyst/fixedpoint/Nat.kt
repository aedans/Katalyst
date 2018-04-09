package io.github.aedans.katalyst.fixedpoint

import arrow.Kind
import arrow.core.*
import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.Coalgebra
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.typeclasses.*

typealias NatPattern = ForOption

/**
 * Natural numbers parameterized by a recursive type combinator.
 */
typealias GNat<T> = Kind<T, NatPattern>

typealias FixNat = GNat<ForFix>
typealias MuNat = GNat<ForMu>
typealias NuNat = GNat<ForNu>

fun toGNatCoalgebra() = Coalgebra<NatPattern, Int> {
    if (it == 0) None else Some(it - 1)
}

fun fromGNatAlgebra() = Algebra<NatPattern, Eval<Int>> {
    it.fix().fold({ Eval.Zero }, { it.map { it + 1 } })
}

inline fun <reified T> Int.toGNat(CT: Corecursive<T>): GNat<T> = CT.run {
    ana(toGNatCoalgebra(), Option.functor())
}

inline fun <reified T> GNat<T>.toInt(RT: Recursive<T>): Int = RT.run {
    cata(fromGNatAlgebra(), Option.functor())
}
