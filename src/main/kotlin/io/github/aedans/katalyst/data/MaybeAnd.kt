package io.github.aedans.katalyst.data

import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.*

/**
 * The pattern functor for a linked list.
 */
@higherkind
data class MaybeAnd<out F, out A>(val value: Option<PairKW<F, A>>) : MaybeAndKind<F, A> {
    fun <B> map(f: (A) -> B): MaybeAnd<F, B> = MaybeAnd(value.map { it.map(f) })
    fun <B> ap(ff: MaybeAnd<*, (A) -> B>) = ff.value.fold({ empty }, { map(it.b) })
    fun <B> flatMap(f: (A) -> MaybeAnd<*, B>) = value.fold({ empty }, { f(it.b) })
    fun <B> foldL(b: B, f: (B, A) -> B) = value.fold({ b }, { f(b, it.b) })
    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = value.fold({ lb }, { f(it.b, lb) })

    companion object {
        operator fun <F, A> invoke(head: F, tail: A) = MaybeAnd(Some(head toKW tail))
        val empty = MaybeAnd<Nothing, Nothing>(None)
        fun <A> pure(a: A) = MaybeAnd(null, a)
    }
}

@instance(MaybeAnd::class)
interface MaybeAndEqInstance : Eq<MaybeAndKind<*, *>> {
    override fun eqv(a: MaybeAndKind<*, *>, b: MaybeAndKind<*, *>) = a.ev() == b.ev()
}

@instance(MaybeAnd::class)
interface MaybeAndFunctorInstance : Functor<MaybeAndKindPartial<*>> {
    override fun <A, B> map(fa: MaybeAndKind<*, A>, f: (A) -> B) = fa.ev().map(f)
}

@instance(MaybeAnd::class)
interface MaybeAndApplicativeInstance : Applicative<MaybeAndKindPartial<*>> {
    override fun <A, B> ap(fa: MaybeAndKind<*, A>, ff: MaybeAndKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A, B> map(fa: MaybeAndKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A> pure(a: A) = MaybeAnd.pure(a)
}

@instance(MaybeAnd::class)
interface MaybeAndMonadInstance : Monad<MaybeAndKindPartial<*>> {
    override fun <A, B> map(fa: MaybeAndKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A, B> ap(fa: MaybeAndKind<*, A>, ff: MaybeAndKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A> pure(a: A) = MaybeAnd.pure(a)
    override fun <A, B> flatMap(fa: MaybeAndKind<*, A>, f: (A) -> MaybeAndKind<*, B>) = fa.ev().flatMap { f(it).ev() }
    override tailrec fun <A, B> tailRecM(a: A, f: (A) -> MaybeAndKind<*, Either<A, B>>): MaybeAnd<*, B> {
        val value = f(a).ev().value
        return when (value) {
            None -> MaybeAnd.empty
            is Some -> {
                val b = value.t.b
                when (b) {
                    is Either.Left -> tailRecM(b.a, f)
                    is Either.Right -> MaybeAnd.pure(b.b)
                }
            }
        }
    }
}

@instance(MaybeAnd::class)
interface MaybeAndFoldableInstance : Foldable<MaybeAndKindPartial<*>> {
    override fun <A, B> foldLeft(fa: MaybeAndKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldRight(fa: MaybeAndKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
}

@instance(MaybeAnd::class)
interface MaybeAndTraverseInstance : Traverse<MaybeAndKindPartial<*>> {
    override fun <A, B> foldLeft(fa: MaybeAndKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldRight(fa: MaybeAndKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
    override fun <G, A, B> traverse(fa: MaybeAndKind<*, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, MaybeAndKind<*, B>> =
            fa.ev().value.fold({ GA.pure(MaybeAnd.empty) }, { GA.map(f(it.b), MaybeAnd.Companion::pure) })
}
