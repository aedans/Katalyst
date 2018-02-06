package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.*
import arrow.typeclasses.*

/**
 * The pattern functor for Cofree.
 */
@higherkind
data class EnvT<out E, out W, out A>(val run: Tuple2<E, HK<W, A>>) : EnvTKind<E, W, A> {
    val ask get() = run.a
    val lower get() = run.b
    companion object
}

fun <E, W, A, B> EnvT<E, W, A>.map(fn: (A) -> B, FW: Functor<W>) =
        EnvT(ask toT FW.map(lower, fn))

fun <E, W, A, B> EnvT<E, W, A>.foldLeft(b: B, f: (B, A) -> B, FW: Foldable<W>) =
        FW.foldLeft(lower, b, f)

fun <E, W, A, B> EnvT<E, W, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FW: Foldable<W>) =
        FW.foldRight(lower, lb, f)

fun <E, W, A, B, G> EnvT<E, W, A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, TW: Traverse<W>) =
        GA.map(TW.traverse(lower, f, GA)) { EnvT(ask toT it) }

fun <E, W, A, B> EnvT<E, W, A>.coflatMap(f: (EnvT<E, W, A>) -> B, FW: Functor<W>) =
        EnvT(ask toT FW.map(lower) { _ -> f(this) })

fun <E, W, A> EnvT<E, W, A>.extract(CW: Comonad<W>) =
        CW.extract(lower)

@instance(EnvT::class)
interface EnvTFunctorInstance<E, W> : Functor<EnvTKindPartial<E, W>> {
    fun FW(): Functor<W>

    override fun <A, B> map(fa: EnvTKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, FW())
}

@instance(EnvT::class)
interface EnvTFoldableInstance<E, W> : Foldable<EnvTKindPartial<E, W>> {
    fun FW(): Foldable<W>

    override fun <A, B> foldLeft(fa: EnvTKind<E, W, A>, b: B, f: (B, A) -> B) =
            fa.ev().foldLeft(b, f, FW())

    override fun <A, B> foldRight(fa: EnvTKind<E, W, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fa.ev().foldRight(lb, f, FW())
}

@instance(EnvT::class)
interface EnvTTraverseInstance<E, W> : Traverse<EnvTKindPartial<E, W>> {
    fun TW(): Traverse<W>

    override fun <A, B> foldLeft(fa: EnvTKind<E, W, A>, b: B, f: (B, A) -> B) =
            fa.ev().foldLeft(b, f, TW())

    override fun <A, B> foldRight(fa: EnvTKind<E, W, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
            fa.ev().foldRight(lb, f, TW())

    override fun <A, B> map(fa: EnvTKind<E, W, A>, f: (A) -> B) =
            fa.ev().map(f, TW())

    override fun <G, A, B> traverse(fa: EnvTKind<E, W, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, EnvTKind<E, W, B>> =
            fa.ev().traverse(f, GA, TW())
}

@instance(EnvT::class)
interface EnvTComonadInstance<E, W> : EnvTFunctorInstance<E, W>, Comonad<EnvTKindPartial<E, W>> {
    override fun FW(): Functor<W> = CW()
    fun CW(): Comonad<W>

    override fun <A, B> coflatMap(fa: EnvTKind<E, W, A>, f: (EnvTKind<E, W, A>) -> B) =
            fa.ev().coflatMap({ f(it.ev()) }, FW())

    override fun <A> extract(fa: EnvTKind<E, W, A>) =
            fa.ev().extract(CW())
}
