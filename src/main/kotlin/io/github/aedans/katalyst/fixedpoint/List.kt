package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.Algebras
import io.github.aedans.katalyst.fixedpoint.ListF.Companion.nil
import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.cata
import kategory.*

@higherkind
data class Cons<out F, out A>(val head: F, val tail: A) : ConsKind<F, A>

@instance(ListF::class)
interface ConsEqInstance : Eq<ConsKind<*, *>> {
    override fun eqv(a: ConsKind<*, *>, b: ConsKind<*, *>) = a == b
}

@higherkind
data class ListF<out F, out A>(val value: Option<Cons<F, A>>) : ListFKind<F, A> {
    fun <B> map(f: (A) -> B): ListF<F, B> = value.map { Cons(it.head, f(it.tail)) }.listF
    fun <B> ap(ff: ListF<*, (A) -> B>) = ff.value.fold({ nil }, { map(it.tail) })
    fun <B> flatMap(f: (A) -> ListF<*, B>) = value.fold({ nil }, { f(it.tail) })
    fun <B> foldL(b: B, f: (B, A) -> B) = value.fold({ b }, { f(b, it.tail) })
    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = value.fold({ lb }, { f(it.tail, lb) })

    companion object {
        val nil = ListF<Nothing, Nothing>(Option.None)
        fun <F, A> cons(head: F, tail: A) = ListF(Option.Some(Cons(head, tail)))
        fun <A> pure(a: A) = cons(null, a)
    }
}

@instance(ListF::class)
interface ListFEqInstance : Eq<ListFKind<*, *>> {
    override fun eqv(a: ListFKind<*, *>, b: ListFKind<*, *>) = a == b
}

@instance(ListF::class)
interface ListFFunctorInstance : Functor<ListFKindPartial<*>> {
    override fun <A, B> map(fa: ListFKind<*, A>, f: (A) -> B) = fa.ev().map(f)
}

@instance(ListF::class)
interface ListFApplicativeInstance : Applicative<ListFKindPartial<*>> {
    override fun <A, B> ap(fa: ListFKind<*, A>, ff: ListFKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
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
                val tail = value.value.tail
                when (tail) {
                    is Either.Left -> tailRecM(tail.a, f)
                    is Either.Right -> ListF.pure(tail.b)
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
            fa.ev().value.fold({ GA.pure(ListF.nil) }, { GA.map(f(it.tail), ListF.Companion::pure) })
}

val <F, A> Option<Cons<F, A>>.listF get() = ListF(this)

typealias ListR<T, A> = HK<T, ListFKindPartial<A>>

fun <A> Algebras.toListR() = Coalgebra<ListFKindPartial<A>, List<A>> {
    if (it.isEmpty()) ListF.nil else ListF.cons(it.first(), it.drop(1))
}

fun <A> Algebras.fromListR() = Algebra<ListFKindPartial<A>, List<A>> {
    it.ev().value.fold({ emptyList() }, { listOf(it.head) + it.tail })
}

inline fun <reified T, A> List<A>.listR(): ListR<T, A> = ana(coalg = Algebras.toListR())
inline fun <reified T, A> ListR<T, A>.list(): List<A> = cata(alg = Algebras.fromListR())
