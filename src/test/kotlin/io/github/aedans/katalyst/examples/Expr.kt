package io.github.aedans.katalyst.examples

import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.data.Fix
import io.github.aedans.katalyst.implicits.cata
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*

// Define an expression pattern type
@higherkind
sealed class ExprP<out A> : ExprPKind<A> {
    class Int(val value: kotlin.Int) : ExprP<Nothing>()
    class Neg<out A>(val expr: A) : ExprP<A>()
    class Plus<out A>(val expr1: A, val expr2: A) : ExprP<A>()
    companion object
}

// Create a Functor instance for the expression pattern
@instance(ExprP::class)
interface ExprPFunctorInstance : Functor<ExprPHK> {
    override fun <A, B> map(fa: ExprPKind<A>, f: (A) -> B) = run {
        val ev = fa.ev()
        when (ev) {
            is ExprP.Int -> ev
            is ExprP.Neg -> ExprP.Neg(f(ev.expr))
            is ExprP.Plus -> ExprP.Plus(f(ev.expr1), f(ev.expr2))
        }
    }
}

// Expand the expression pattern with a recursive type
typealias Expr = Fix<ExprPHK>

// Define convenience functions for constructing expressions
fun int(i: Int) = Fix(ExprP.Int(i))
fun neg(e: Expr) = Fix(ExprP.Neg(e))
fun plus(a: Expr, b: Expr) = Fix(ExprP.Plus(a, b))

// Define an algebra to evaluate an expression
val evalAlgebra = Algebra<ExprPHK, Int> {
    val ev = it.ev()
    when (ev) {
        is ExprP.Int -> ev.value
        is ExprP.Neg -> -ev.expr
        is ExprP.Plus -> ev.expr1 + ev.expr2
    }
}

// Define an algebra to show an expression
val showAlgebra = Algebra<ExprPHK, String> {
    val ev = it.ev()
    when (ev) {
        is ExprP.Int -> ev.value.toString()
        is ExprP.Neg -> "-${ev.expr}"
        is ExprP.Plus -> "${ev.expr1} + ${ev.expr2}"
    }
}

// Use morphisms to generically apply algebras
fun main(args: Array<String>) {
    val expr = plus(int(1), int(2))
    expr.cata(alg = evalAlgebra) // 3
    expr.cata(alg = showAlgebra) // "1 + 2"
}

class ExprTest : UnitSpec() {
    init {
        testLaws(FunctorLaws.laws(ExprP.functor(), { ExprP.Int(it) }, Eq.any()))

        "Int should evaluate to it" {
            forAll(Gen.int()) {
                int(it).cata(alg = evalAlgebra) == it
            }
        }

        "Int should show it" {
            forAll(Gen.int()) {
                int(it).cata(alg = showAlgebra) == it.toString()
            }
        }

        "Neg should evaluate to -it" {
            forAll(Gen.int()) {
                neg(int(it)).cata(alg = evalAlgebra) == -it
            }
        }

        "Neg should show -it" {
            forAll(Gen.int()) {
                neg(int(it)).cata(alg = showAlgebra) == "-$it"
            }
        }

        "Plus should evaluate to a + b" {
            forAll(Gen.int(), Gen.int()) { a, b ->
                plus(int(a), int(b)).cata(alg = evalAlgebra) == a + b
            }
        }

        "Plus should show a + b" {
            forAll(Gen.int(), Gen.int()) { a, b ->
                plus(int(a), int(b)).cata(alg = showAlgebra) == "$a + $b"
            }
        }
    }
}
