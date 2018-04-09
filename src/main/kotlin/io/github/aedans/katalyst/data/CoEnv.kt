package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.*
import arrow.core.Either.*
import arrow.typeclasses.*

/**
 * The pattern functor for Free.
 */
@higherkind
data class CoEnv<out E, out W, out A>(val run: Either<E, Kind<W, A>>) : CoEnvOf<E, W, A> {
    companion object
}

fun <E, W, A, B> CoEnv<E, W, A>.map(f: (A) -> B, FW: Functor<W>) = FW.run {
    CoEnv(run.map { it.map(f) })
}

fun <E, W, A, B> CoEnv<E, W, A>.ap(ff: CoEnv<E, W, (A) -> B>, MW: Monad<W>) = MW.run {
    CoEnv(run.flatMap { lower -> ff.run.map { it.flatMap { f -> lower.map { f(it) } } } })
}

fun <A, W> CoEnv.Companion.just(a: A, MW: Monad<W>) = MW.run {
    CoEnv(Right(a.just()))
}

fun <E, W, A, B> CoEnv<E, W, A>.flatMap(f: (A) -> CoEnv<E, W, B>,
                                        MW: Monad<W>,
                                        TW: Traverse<W>,
                                        AC: Applicative<CoEnvPartialOf<E, W>>) = MW.run {
    TW.run {
        CoEnv(run.flatMap { it.traverse(AC, f).fix().run.map { it.flatten() } })
    }
}

tailrec fun <E, W, A, B> CoEnv.Companion.tailRecM(a: A, f: (A) -> CoEnv<E, W, Either<A, B>>,
                                                  MW: Monad<W>,
                                                  TW: Traverse<W>): CoEnv<E, W, B> {
    val fa = f(a)
    return when (fa.run) {
        is Left -> CoEnv(Left(fa.run.a))
        is Right -> {
            val either = TW.run { fa.run.b.sequence(Either.applicative()).fix() }
            when (either) {
                is Left -> tailRecM(either.a, f, MW, TW)
                is Right -> CoEnv(Right(either.b))
            }
        }
    }
}

fun <E, W, A, B> CoEnv<E, W, A>.foldLeft(b: B, f: (B, A) -> B, FW: Foldable<W>) = FW.run {
    run.fold({ b }, { it.foldLeft(b, f) })
}

fun <E, W, A, B> CoEnv<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FW: Foldable<W>) = FW.run {
    run.fold({ lb }, { it.foldRight(lb, f) })
}

fun <E, W, A, B, G> CoEnv<E, W, A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>, TW: Traverse<W>) = TW.run {
    GA.run {
        run.fold({ CoEnv<E, W, B>(Left(it)).just() }, { it.traverse(GA, f).map { CoEnv(Right(it)) } })
    }
}

@instance(CoEnv::class)
interface CoEnvEqInstance<E, W, A> : Eq<CoEnvOf<E, W, A>> {
    override fun CoEnvOf<E, W, A>.eqv(b: CoEnvOf<E, W, A>) = fix() == b.fix()
}

@instance(CoEnv::class)
interface CoEnvFunctorInstance<E, W> : Functor<CoEnvPartialOf<E, W>> {
    fun FW(): Functor<W>

    override fun <A, B> CoEnvOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, FW())
}

@instance(CoEnv::class)
interface CoEnvApplicativeInstance<E, W> : Applicative<CoEnvPartialOf<E, W>> {
    fun MW(): Monad<W>

    override fun <A, B> CoEnvOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, MW())

    override fun <A, B> CoEnvOf<E, W, A>.ap(ff: CoEnvOf<E, W, (A) -> B>) =
            fix().ap(ff.fix(), MW())

    override fun <A> just(a: A) = CoEnv.just(a, MW())
}

@instance(CoEnv::class)
interface CoEnvMonadInstance<E, W> : Monad<CoEnvPartialOf<E, W>> {
    fun MW(): Monad<W>

    fun TW(): Traverse<W>

    override fun <A, B> CoEnvOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, MW())

    override fun <A, B> CoEnvOf<E, W, A>.ap(ff: CoEnvOf<E, W, (A) -> B>) =
            fix().ap(ff.fix(), MW())

    override fun <A> just(a: A) =
            CoEnv.just(a, MW())

    override fun <A, B> CoEnvOf<E, W, A>.flatMap(f: (A) -> CoEnvOf<E, W, B>) =
            fix().flatMap({ f(it).fix() }, MW(), TW(), this@CoEnvMonadInstance)

    override fun <A, B> tailRecM(a: A, f: (A) -> CoEnvOf<E, W, Either<A, B>>) =
            CoEnv.tailRecM(a, { f(it).fix() }, MW(), TW())
}

@instance(CoEnv::class)
interface CoEnvFoldableInstance<E, W> : Foldable<CoEnvPartialOf<E, W>> {
    fun FW(): Foldable<W>

    override fun <A, B> CoEnvOf<E, W, A>.foldLeft(b: B, f: (B, A) -> B) =
            fix().foldLeft(b, f, FW())

    override fun <A, B> CoEnvOf<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fix().foldRight(lb, f, FW())
}

@instance(CoEnv::class)
interface CoEnvTraverseInstance<E, W> : Traverse<CoEnvPartialOf<E, W>> {
    fun TW(): Traverse<W>

    override fun <A, B> CoEnvOf<E, W, A>.foldLeft(b: B, f: (B, A) -> B) =
            fix().foldLeft(b, f, TW())

    override fun <A, B> CoEnvOf<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fix().foldRight(lb, f, TW())

    override fun <A, B> CoEnvOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, TW())

    override fun <G, A, B> CoEnvOf<E, W, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>) =
            fix().traverse(f, AP, TW())
}
