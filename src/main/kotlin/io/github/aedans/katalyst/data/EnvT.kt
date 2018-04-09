package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.*
import arrow.typeclasses.*

/**
 * The pattern functor for Cofree.
 */
@higherkind
data class EnvT<out E, out W, out A>(val run: Tuple2<E, Kind<W, A>>) : EnvTOf<E, W, A> {
    val ask get() = run.a
    val lower get() = run.b
    companion object
}

fun <E, W, A, B> EnvT<E, W, A>.map(f: (A) -> B, FW: Functor<W>) = FW.run {
    EnvT(ask toT lower.map(f))
}

fun <E, W, A, B> EnvT<E, W, A>.coflatMap(f: (EnvT<E, W, A>) -> B, FW: Functor<W>) = FW.run {
    EnvT(ask toT lower.map { _ -> f(this@coflatMap) })
}

fun <E, W, A> EnvT<E, W, A>.extract(CW: Comonad<W>) = CW.run { lower.extract() }

fun <E, W, A, B> EnvT<E, W, A>.foldLeft(b: B, f: (B, A) -> B, FW: Foldable<W>) = FW.run { lower.foldLeft(b, f) }

fun <E, W, A, B> EnvT<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FW: Foldable<W>) = FW.run { lower.foldRight(lb, f) }

fun <E, W, A, B, G> EnvT<E, W, A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>, TW: Traverse<W>) = TW.run {
    GA.run {
        lower.traverse(GA, f).map { EnvT(ask toT it) }
    }
}

@instance(EnvT::class)
interface EnvTEqInstance<E, W, A> : Eq<EnvTOf<E, W, A>> {
    override fun EnvTOf<E, W, A>.eqv(b: EnvTOf<E, W, A>) = fix() == b.fix()
}

@instance(EnvT::class)
interface EnvTFunctorInstance<E, W> : Functor<EnvTPartialOf<E, W>> {
    fun FW(): Functor<W>

    override fun <A, B> EnvTOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, FW())
}

@instance(EnvT::class)
interface EnvTComonadInstance<E, W> : EnvTFunctorInstance<E, W>, Comonad<EnvTPartialOf<E, W>> {
    override fun FW(): Functor<W> = CW()
    fun CW(): Comonad<W>

    override fun <A, B> EnvTOf<E, W, A>.coflatMap(f: (EnvTOf<E, W, A>) -> B) =
            fix().coflatMap({ f(it.fix()) }, FW())

    override fun <A> EnvTOf<E, W, A>.extract() =
            fix().extract(CW())
}

@instance(EnvT::class)
interface EnvTFoldableInstance<E, W> : Foldable<EnvTPartialOf<E, W>> {
    fun FW(): Foldable<W>

    override fun <A, B> EnvTOf<E, W, A>.foldLeft(b: B, f: (B, A) -> B) =
            fix().foldLeft(b, f, FW())

    override fun <A, B> EnvTOf<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fix().foldRight(lb, f, FW())
}

@instance(EnvT::class)
interface EnvTTraverseInstance<E, W> : Traverse<EnvTPartialOf<E, W>> {
    fun TW(): Traverse<W>

    override fun <A, B> EnvTOf<E, W, A>.foldLeft(b: B, f: (B, A) -> B) =
            fix().foldLeft(b, f, TW())

    override fun <A, B> EnvTOf<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fix().foldRight(lb, f, TW())

    override fun <A, B> EnvTOf<E, W, A>.map(f: (A) -> B) =
            fix().map(f, TW())

    override fun <G, A, B> EnvTOf<E, W, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>) =
            fix().traverse(f, AP, TW())
}
