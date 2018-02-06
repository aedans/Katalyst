package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.*
import arrow.core.Either.*
import arrow.typeclasses.*

/**
 * The pattern functor for Free.
 */
@higherkind
data class CoEnv<out E, out W, out A>(val run: Either<E, HK<W, A>>) : CoEnvKind<E, W, A> {
    companion object
}

fun <E, W, A, B> CoEnv<E, W, A>.map(f: (A) -> B, FW: Functor<W>) =
        CoEnv(run.map { FW.map(it, f) })

fun <E, W, A, B> CoEnv<E, W, A>.ap(ff: CoEnv<E, W, (A) -> B>, MW: Monad<W>) =
        CoEnv(run.flatMap { lower -> ff.run.map { MW.flatMap(it) { f -> MW.map(lower) { f(it) } } } })

fun <A, W> CoEnv.Companion.pure(a: A, MW: Monad<W>) =
        CoEnv(Right(MW.pure(a)))

fun <E, W, A, B> CoEnv<E, W, A>.flatMap(f: (A) -> CoEnv<E, W, B>,
                                        MW: Monad<W>, TW: Traverse<W>, AC: Applicative<CoEnvKindPartial<E, W>>) =
        CoEnv(run.flatMap { TW.traverse(it, f, AC).ev().run.map(MW::flatten) })

tailrec fun <E, W, A, B> CoEnv.Companion.tailRecM(a: A, f: (A) -> CoEnv<E, W, Either<A, B>>, MW: Monad<W>, TW: Traverse<W>): CoEnv<E, W, B> {
    val fa = f(a)
    return when (fa.run) {
        is Left -> CoEnv(Left(fa.run.a))
        is Right -> {
            val either = TW.sequence(fa.run.b).ev()
            when (either) {
                is Left -> tailRecM(either.a, f, MW, TW)
                is Right -> CoEnv(Right(either.b))
            }
        }
    }
}

fun <E, W, A, B> CoEnv<E, W, A>.foldLeft(b: B, f: (B, A) -> B, FW: Foldable<W>) =
        run.fold({ b }, { FW.foldLeft(it, b, f) })

fun <E, W, A, B> CoEnv<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FW: Foldable<W>) =
        run.fold({ lb }, { FW.foldRight(it, lb, f) })

fun <E, W, A, B, G> CoEnv<E, W, A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, TW: Traverse<W>) =
        run.fold({ GA.pure(CoEnv<E, W, B>(Left(it))) }, { GA.map(TW.traverse(it, f, GA)) { CoEnv(Right(it)) } })

@instance(CoEnv::class)
interface CoEnvEqInstance<E, W, A> : Eq<CoEnvKind<E, W, A>> {
    override fun eqv(a: CoEnvKind<E, W, A>, b: CoEnvKind<E, W, A>) = a.ev() == b.ev()
}

@instance(CoEnv::class)
interface CoEnvFunctorInstance<E, W> : Functor<CoEnvKindPartial<E, W>> {
    fun FW(): Functor<W>

    override fun <A, B> map(fa: CoEnvKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, FW())
}

@instance(CoEnv::class)
interface CoEnvApplicativeInstance<E, W> : Applicative<CoEnvKindPartial<E, W>> {
    fun MW(): Monad<W>

    override fun <A, B> map(fa: CoEnvKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, MW())

    override fun <A, B> ap(fa: CoEnvKind<E, W, A>, ff: CoEnvKind<E, W, (A) -> B>) =
            fa.ev().ap(ff.ev(), MW())

    override fun <A> pure(a: A) =
            CoEnv.pure(a, MW())
}

@instance(CoEnv::class)
interface CoEnvMonadInstance<E, W> : Monad<CoEnvKindPartial<E, W>> {
    fun MW(): Monad<W>

    fun TW(): Traverse<W>

    override fun <A, B> map(fa: CoEnvKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, MW())

    override fun <A, B> ap(fa: CoEnvKind<E, W, A>, ff: CoEnvKind<E, W, (A) -> B>) =
            fa.ev().ap(ff.ev(), MW())

    override fun <A> pure(a: A) =
            CoEnv.pure(a, MW())

    override fun <A, B> flatMap(fa: CoEnvKind<E, W, A>, f: (A) -> CoEnvKind<E, W, B>) =
            fa.ev().flatMap({ f(it).ev() }, MW(), TW(), this)

    override fun <A, B> tailRecM(a: A, f: (A) -> CoEnvKind<E, W, Either<A, B>>) =
            CoEnv.tailRecM(a, { f(it).ev() }, MW(), TW())
}

@instance(CoEnv::class)
interface CoEnvFoldableInstance<E, W> : Foldable<CoEnvKindPartial<E, W>> {
    fun FW(): Foldable<W>

    override fun <A, B> foldLeft(fa: CoEnvKind<E, W, A>, b: B, f: (B, A) -> B) =
            fa.ev().foldLeft(b, f, FW())

    override fun <A, B> foldRight(fa: CoEnvKind<E, W, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fa.ev().foldRight(lb, f, FW())
}

@instance(CoEnv::class)
interface CoEnvTraverseInstance<E, W> : Traverse<CoEnvKindPartial<E, W>> {
    fun TW(): Traverse<W>

    override fun <A, B> foldLeft(fa: CoEnvKind<E, W, A>, b: B, f: (B, A) -> B) =
            fa.ev().foldLeft(b, f, TW())

    override fun <A, B> foldRight(fa: CoEnvKind<E, W, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fa.ev().foldRight(lb, f, TW())

    override fun <A, B> map(fa: CoEnvKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, TW())

    override fun <G, A, B> traverse(fa: CoEnvKind<E, W, A>, f: (A) -> HK<G, B>, GA: Applicative<G>) =
            fa.ev().traverse(f, GA, TW())
}
