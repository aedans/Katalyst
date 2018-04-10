package io.github.aedans.katalyst.examples

import arrow.*
import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.*
import io.github.aedans.katalyst.Algebra
import io.github.aedans.katalyst.data.*
import io.kotlintest.matchers.shouldEqual

// Define an expression pattern type
@higherkind
sealed class ExprPattern<out A> : ExprPatternOf<A> {
    class Int(val value: kotlin.Int) : ExprPattern<Nothing>()
    class Neg<out A>(val expr: A) : ExprPattern<A>()
    class Plus<out A>(val expr1: A, val expr2: A) : ExprPattern<A>()
    companion object
}

// Create a Functor instance for the expression pattern
@instance(ExprPattern::class)
interface ExprPatternFunctorInstance : Functor<ForExprPattern> {
    override fun <A, B> ExprPatternOf<A>.map(f: (A) -> B) = run {
        val fix = fix()
        when (fix) {
            is ExprPattern.Int -> fix
            is ExprPattern.Neg -> ExprPattern.Neg(f(fix.expr))
            is ExprPattern.Plus -> ExprPattern.Plus(f(fix.expr1), f(fix.expr2))
        }
    }
}

// Expand the expression pattern with a recursive type
typealias Expr = FixOf<ForExprPattern>

// Define convenience functions for constructing expressions
fun int(i: Int) = Fix(ExprPattern.Int(i))
fun neg(e: Expr) = Fix(ExprPattern.Neg(Eval.now(e)))
fun plus(a: Expr, b: Expr) = Fix(ExprPattern.Plus(Eval.now(a), Eval.now(b)))

// Define an algebra to evaluate an expression
fun evalExprAlgebra() = Algebra<ForExprPattern, Eval<Int>> {
    val fix = it.fix()
    when (fix) {
        is ExprPattern.Int -> Eval.now(fix.value)
        is ExprPattern.Neg -> fix.expr.map { -it }
        is ExprPattern.Plus -> Eval.monad().binding { fix.expr1.bind() + fix.expr2.bind() }.fix()
    }
}

// Use recursion schemes to generically apply algebras
fun main(args: Array<String>) {
    val expr = plus(plus(int(1), int(2)), neg(plus(int(3), int(4))))
    Fix.recursive().run {
        expr.cata(evalExprAlgebra(), ExprPattern.functor()) // -4
    }
}

class ExprTest : UnitSpec() {
    init {
        testLaws(FunctorLaws.laws(ExprPattern.functor(), { ExprPattern.Int(it) }, Eq.any()))

        val expr = plus(plus(int(1), int(2)), neg(plus(int(3), int(4))))

        "expr.cata(alg = evalExprAlgebra()) should be -4" {
            Fix.recursive().run {
                expr.cata(evalExprAlgebra(), ExprPattern.functor())
            } shouldEqual -4
        }
    }
}
