package io.github.aedans.katalyst.fixedpoint

import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.AndMaybe.Companion.empty
import io.github.aedans.katalyst.syntax.*

/**
 * The pattern functor for a linked list.
 */
@higherkind
data class AndMaybe<out F, out A>(val value: Option<PairKW<F, A>>) : AndMaybeKind<F, A> {
    fun <B> map(f: (A) -> B): AndMaybe<F, B> = AndMaybe(value.map { it.map(f) })
    fun <B> ap(ff: AndMaybe<*, (A) -> B>) = ff.value.fold({ empty }, { map(it.b) })
    fun <B> flatMap(f: (A) -> AndMaybe<*, B>) = value.fold({ empty }, { f(it.b) })
    fun <B> foldL(b: B, f: (B, A) -> B) = value.fold({ b }, { f(b, it.b) })
    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = value.fold({ lb }, { f(it.b, lb) })

    companion object {
        val empty = AndMaybe<Nothing, Nothing>(None)
        fun <F, A> cons(head: F, tail: A) = AndMaybe(Some(head toKW tail))
        fun <A> pure(a: A) = cons(null, a)
    }
}

@instance(AndMaybe::class)
interface AndMaybeEqInstance : Eq<AndMaybeKind<*, *>> {
    override fun eqv(a: AndMaybeKind<*, *>, b: AndMaybeKind<*, *>) = a.ev() == b.ev()
}

@instance(AndMaybe::class)
interface AndMaybeFunctorInstance : Functor<AndMaybeKindPartial<*>> {
    override fun <A, B> map(fa: AndMaybeKind<*, A>, f: (A) -> B) = fa.ev().map(f)
}

@instance(AndMaybe::class)
interface AndMaybeApplicativeInstance : Applicative<AndMaybeKindPartial<*>> {
    override fun <A, B> ap(fa: AndMaybeKind<*, A>, ff: AndMaybeKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A, B> map(fa: AndMaybeKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A> pure(a: A) = AndMaybe.pure(a)
}

@instance(AndMaybe::class)
interface AndMaybeMonadInstance : Monad<AndMaybeKindPartial<*>> {
    override fun <A, B> map(fa: AndMaybeKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A, B> ap(fa: AndMaybeKind<*, A>, ff: AndMaybeKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A> pure(a: A) = AndMaybe.pure(a)
    override fun <A, B> flatMap(fa: AndMaybeKind<*, A>, f: (A) -> AndMaybeKind<*, B>) = fa.ev().flatMap { f(it).ev() }
    override tailrec fun <A, B> tailRecM(a: A, f: (A) -> AndMaybeKind<*, Either<A, B>>): AndMaybe<*, B> {
        val value = f(a).ev().value
        return when (value) {
            None -> empty
            is Some -> {
                val b = value.t.b
                when (b) {
                    is Either.Left -> tailRecM(b.a, f)
                    is Either.Right -> AndMaybe.pure(b.b)
                }
            }
        }
    }
}

@instance(AndMaybe::class)
interface AndMaybeFoldableInstance : Foldable<AndMaybeKindPartial<*>> {
    override fun <A, B> foldLeft(fa: AndMaybeKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldRight(fa: AndMaybeKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
}

@instance(AndMaybe::class)
interface AndMaybeTraverseInstance : Traverse<AndMaybeKindPartial<*>> {
    override fun <A, B> foldLeft(fa: AndMaybeKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldRight(fa: AndMaybeKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
    override fun <G, A, B> traverse(fa: AndMaybeKind<*, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, AndMaybeKind<*, B>> =
            fa.ev().value.fold({ GA.pure(AndMaybe.empty) }, { GA.map(f(it.b), AndMaybe.Companion::pure) })
}

/**
 * Recursive list parameterized by a recursive type combinator.
 */
typealias ListR<T, A> = HK<T, AndMaybeKindPartial<A>>

fun <A> toListRCoalgebra() = Coalgebra<AndMaybeKindPartial<A>, List<A>> {
    if (it.isEmpty()) AndMaybe.empty else AndMaybe.cons(it.first(), it.drop(1))
}

fun <A> fromListRAlgebra() = Algebra<AndMaybeKindPartial<A>, List<A>> {
    it.ev().value.fold({ emptyList() }, { listOf(it.a) + it.b })
}

inline fun <reified T, A> List<A>.toListR(): ListR<T, A> = ana(coalg = toListRCoalgebra())
inline fun <reified T, A> ListR<T, A>.toList(): List<A> = cata(alg = fromListRAlgebra())
