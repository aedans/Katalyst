Katalyst
========

**Notice: Katalyst has been merged with Arrow to create arrow-recursion and is no longer being maintained. See [The Arrow Website](https://arrow-kt.io/) and [The Arrow Recursion Docs](https://arrow-kt.io/docs/recursion/intro/) instead.**

[![Download](https://api.bintray.com/packages/aedans/maven/katalyst/images/download.svg)](https://bintray.com/aedans/maven/katalyst/_latestVersion)

[Kotlin](http://kotlinlang.org) recursion schemes with [Arrow](https://github.com/arrow-kt/arrow).

Gradle
------

```gradle
repositories {
    maven { url 'https://dl.bintray.com/aedans/maven/' }
}

dependencies {
    compile 'io.github.aedans:katalyst:$katalyst_version'
}
```

Features
--------

- [x] Mu, Nu, and Fix data types
- [x] Recursive, Corecursive, and Birecursive typeclasses
- [x] Cata, ana, and hylo recursion schemes
- [x] EnvT and CoEnv data types
- [x] List and Nat defined using recursion schemes
- [x] Kleisli recursion schemes
- [x] Generalized recursion schemes
- [x] Advanced recursion schemes (para, apo, histo, futu, etc.)
- [x] Free and Cofree defined using recursion schemes
- [ ] Recursive and Corecursive instances for Free and Cofree
- [ ] Elgot recursion schemes

Code sample
-----------

```kotlin
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
        is ExprPattern.Plus -> Eval.monad().binding { fix.expr1.bind() + fix.expr2.bind() }.ev()
    }
}

// Use recursion schemes to generically apply algebras
fun main(args: Array<String>) {
    val expr = plus(plus(int(1), int(2)), neg(plus(int(3), int(4))))
    Fix.recursive().run {
        expr.cata(evalExprAlgebra(), ExprPattern.functor()) // -4
    }
}
```

Resources
---------

[Recursion Schemes](https://github.com/ekmett/recursion-schemes), the
original Haskell implementation.

[Matryoshka](https://github.com/slamdata/matryoshka), which
much of Katalyst's code is based off of.
