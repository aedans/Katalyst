package io.github.aedans.katalyst.examples

import arrow.*
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.syntax.*
import io.kotlintest.matchers.shouldBe

// Define an expression pattern type
@higherkind
sealed class ExprP<out A>(val isAtomic: Boolean) : ExprPKind<A> {
    class Int(val value: kotlin.Int) : ExprP<Nothing>(true)
    class Neg<out A>(val expr: A) : ExprP<A>(true)
    class Plus<out A>(val expr1: A, val expr2: A) : ExprP<A>(false)
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
typealias Expr = FixKind<ExprPHK>

// Define convenience functions for constructing expressions
fun int(i: Int) = Fix(ExprP.Int(i))
fun neg(e: Expr) = Fix(ExprP.Neg(e))
fun plus(a: Expr, b: Expr) = Fix(ExprP.Plus(a, b))

// Define an algebra to evaluate an expression
fun evalExprAlgebra() = Algebra<ExprPHK, Int> {
    val ev = it.ev()
    when (ev) {
        is ExprP.Int -> ev.value
        is ExprP.Neg -> -ev.expr
        is ExprP.Plus -> ev.expr1 + ev.expr2
    }
}

// Define an algebra to show an expression
fun showExprAlgebra() = GAlgebra<PairKWKindPartial<Expr>, ExprPHK, String> {
    val ev = it.ev()
    when (ev) {
        is ExprP.Int -> ev.value.toString()
        is ExprP.Neg -> {
            val (negated, str) = ev.expr.ev()
            if (negated.ev().unfix.ev().isAtomic) "-$str" // if the negated expression is atomic, parentheses are redundant
            else "-($str)"
        }
        is ExprP.Plus -> {
            val (plus1, str1) = ev.expr1.ev()
            val (plus2, str2) = ev.expr2.ev()
            val strA = if (plus1.ev().unfix.ev().isAtomic) str1 else "($str1)"
            val strB = if (plus2.ev().unfix.ev().isAtomic) str2 else "($str2)"
            "$strA + $strB"
        }
    }
}

// Use recursion schemes to generically apply algebras
fun main(args: Array<String>) {
    val expr = plus(plus(int(1), int(2)), neg(plus(int(3), int(4))))
    expr.cata(alg = evalExprAlgebra()) // -4
    expr.para(gAlg = showExprAlgebra()) // (1 + 2) + -(3 + 4)
}

class ExprTest : UnitSpec() {
    init {
        testLaws(FunctorLaws.laws(ExprP.functor(), { ExprP.Int(it) }, Eq.any()))

        val expr = plus(plus(int(1), int(2)), neg(plus(int(3), int(4))))

        "expr.cata(alg = evalExprAlgebra()) should be -4" {
            expr.cata(alg = evalExprAlgebra()) shouldBe -4
        }

        "expr.para(gAlg = showExprAlgebra()) should be 1 + -(2 + 3)" {
            expr.para(gAlg = showExprAlgebra()) shouldBe "(1 + 2) + -(3 + 4)"
        }
    }
}
