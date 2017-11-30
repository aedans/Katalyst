Katalyst
========

[![Download](https://api.bintray.com/packages/aedans/maven/katalyst/images/download.svg)](https://bintray.com/aedans/maven/katalyst/_latestVersion)

[Kotlin](http://kotlinlang.org) recursion schemes with [Kategory](https://github.com/kategory/kategory).

Gradle
------

```gradle
repositories {
    maven { url 'https://dl.bintray.com/aedans/maven/' }
}

dependencies {
    compile 'io.github.aedans:katalyst:0.3.1'
}
```

Features
--------

- [x] Mu, Nu, and Fix data types
- [x] Recursive, Corecursive, and Birecursive typeclasses
- [x] Cata, ana, and hylo recursion schemes
- [x] List and Nat defined using recursion schemes
- [x] Kleisli recursion schemes
- [x] Generalized recursion schemes
- [ ] Advanced recursion schemes (para, apo, histo, futu, etc.)
- [ ] Elgot recursion schemes
- [ ] EnvT and CoEnv data types
- [ ] Free and Cofree defined using recursion schemes
- [ ] Recursive and Corecursive instances for Free and Cofree 

Code sample
-----------

```kotlin
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

// Use recursion schemes to generically apply algebras
fun main(args: Array<String>) {
    val expr = plus(int(1), int(2))
    expr.cata(alg = evalAlgebra) // 3
    expr.cata(alg = showAlgebra) // "1 + 2"
}
```

Contributing
------------

Katalyst is far from being complete, and any help is greatly
appreciated. If you find a bug, please open an issue. If you just want
to write some code, there are plenty of features that need to be implemented;
just open a pull request and hack at it.

Resources
---------

[Recursion Schemes](https://github.com/ekmett/recursion-schemes), the
original Haskell implementation.

[Matryoshka](https://github.com/slamdata/matryoshka), which
much of Katalyst's code is based off of.
