package io.github.aedans.katalyst

import kategory.*

@higherkind
data class PairKW<out A, out B>(val a: A, val b: B) : PairKWKind<A, B> {
    companion object
}

@instance(PairKW::class)
interface PairKWFunctorInstance<F> : Functor<PairKWKindPartial<F>> {
    override fun <A, B> map(fa: PairKWKind<F, A>, f: (A) -> B) = fa.ev().run { a toKW f(b) }
}

@instance(PairKW::class)
interface PairKWFoldableInstance : Foldable<PairKWKindPartial<*>> {
    override fun <A, B> foldL(fa: PairKWKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().let { f(b, it.b) }
    override fun <A, B> foldR(fa: PairKWKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().let { f(it.b, lb) }
}

@instance(PairKW::class)
interface PairKWTraverseInstance<F> : Traverse<PairKWKindPartial<F>> {
    override fun <A, B> foldL(fa: PairKWKind<F, A>, b: B, f: (B, A) -> B) = PairKW.foldable().foldL(fa, b, f)
    override fun <A, B> foldR(fa: PairKWKind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = PairKW.foldable().foldR(fa, lb, f)
    override fun <G, A, B> traverse(fa: PairKWKind<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>) = fa.ev().run { GA.map(f(b), a::toKW) }
}

infix fun <A, B> A.toKW(b: B) = PairKW(this, b)

fun <A> square(a: A) = PairKW(a, a)
