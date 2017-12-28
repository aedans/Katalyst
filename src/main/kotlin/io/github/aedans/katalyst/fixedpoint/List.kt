package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.*
import io.github.aedans.katalyst.fixedpoint.ListF.Companion.nil
import io.github.aedans.katalyst.syntax.*
import kategory.*

@higherkind
data class ListF<out F, out A>(val value: Option<PairKW<F, A>>) : ListFKind<F, A> {
    fun <B> map(f: (A) -> B): ListF<F, B> = ListF(value.map { it.map(f) })
    fun <B> ap(ff: ListF<*, (A) -> B>) = ff.value.fold({ nil }, { map(it.b) })
    fun <B> flatMap(f: (A) -> ListF<*, B>) = value.fold({ nil }, { f(it.b) })
    fun <B> foldL(b: B, f: (B, A) -> B) = value.fold({ b }, { f(b, it.b) })
    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = value.fold({ lb }, { f(it.b, lb) })

    companion object {
        val nil = ListF<Nothing, Nothing>(Option.None)
        fun <F, A> cons(head: F, tail: A) = ListF(Option.Some(head toKW tail))
        fun <A> pure(a: A) = cons(null, a)
    }
}

@instance(ListF::class)
interface ListFEqInstance : Eq<ListFKind<*, *>> {
    override fun eqv(a: ListFKind<*, *>, b: ListFKind<*, *>) = a.ev() == b.ev()
}

@instance(ListF::class)
interface ListFFunctorInstance : Functor<ListFKindPartial<*>> {
    override fun <A, B> map(fa: ListFKind<*, A>, f: (A) -> B) = fa.ev().map(f)
}

@instance(ListF::class)
interface ListFApplicativeInstance : Applicative<ListFKindPartial<*>> {
    override fun <A, B> ap(fa: ListFKind<*, A>, ff: ListFKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A, B> map(fa: ListFKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A> pure(a: A) = ListF.pure(a)
}

@instance(ListF::class)
interface ListFMonadInstance : Monad<ListFKindPartial<*>> {
    override fun <A, B> map(fa: ListFKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A, B> ap(fa: ListFKind<*, A>, ff: ListFKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A> pure(a: A) = ListF.pure(a)
    override fun <A, B> flatMap(fa: ListFKind<*, A>, f: (A) -> ListFKind<*, B>) = fa.ev().flatMap { f(it).ev() }
    override tailrec fun <A, B> tailRecM(a: A, f: (A) -> ListFKind<*, Either<A, B>>): ListF<*, B> {
        val value = f(a).ev().value
        return when (value) {
            Option.None -> nil
            is Option.Some -> {
                val b = value.value.b
                when (b) {
                    is Either.Left -> tailRecM(b.a, f)
                    is Either.Right -> ListF.pure(b.b)
                }
            }
        }
    }
}

@instance(ListF::class)
interface ListFFoldableInstance : Foldable<ListFKindPartial<*>> {
    override fun <A, B> foldL(fa: ListFKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldR(fa: ListFKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
}

@instance(ListF::class)
interface ListFTraverseInstance : Traverse<ListFKindPartial<*>> {
    override fun <A, B> foldL(fa: ListFKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldR(fa: ListFKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
    override fun <G, A, B> traverse(fa: ListFKind<*, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ListFKind<*, B>> =
            fa.ev().value.fold({ GA.pure(ListF.nil) }, { GA.map(f(it.b), ListF.Companion::pure) })
}

typealias ListR<T, A> = HK<T, ListFKindPartial<A>>

fun <A> Algebras.toListR() = Coalgebra<ListFKindPartial<A>, List<A>> {
    if (it.isEmpty()) ListF.nil else ListF.cons(it.first(), it.drop(1))
}

fun <A> Algebras.fromListR() = Algebra<ListFKindPartial<A>, List<A>> {
    it.ev().value.fold({ emptyList() }, { listOf(it.a) + it.b })
}

inline fun <reified T, A> List<A>.listR(): ListR<T, A> = ana(coalg = Algebras.toListR())
inline fun <reified T, A> ListR<T, A>.list(): List<A> = cata(alg = Algebras.fromListR())
