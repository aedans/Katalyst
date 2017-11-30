package io.github.aedans.katalyst

import kategory.*

@higherkind
data class PairKW<out F, out A>(val a: F, val b: A) : PairKWKind<F, A> {
    fun <B> map(f: (A) -> B) = a toKW f(b)
    fun <B> ap(f: PairKW<*, (A) -> B>) = map(f.b)
    fun <B> flatMap(f: (A) -> PairKW<*, B>) = f(b)
    fun <B> foldL(b: B, f: (B, A) -> B) = f(b, this.b)
    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = f(b, lb)
    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>) = GA.map(f(b), a::toKW)

    companion object {
        fun <A> pure(a: A) = null toKW a
    }
}

@instance(PairKW::class)
interface PairKWEqInstance : Eq<PairKWKind<*, *>> {
    override fun eqv(a: PairKWKind<*, *>, b: PairKWKind<*, *>) = a.ev() == b.ev()
}

@instance(PairKW::class)
interface PairKWFunctorInstance<F> : Functor<PairKWKindPartial<F>> {
    override fun <A, B> map(fa: PairKWKind<F, A>, f: (A) -> B) = fa.ev().map(f)
}

@instance(PairKW::class)
interface PairKWApplicativeInstance : Applicative<PairKWKindPartial<*>> {
    override fun <A> pure(a: A) = PairKW.pure(a)
    override fun <A, B> map(fa: PairKWKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A, B> ap(fa: PairKWKind<*, A>, ff: PairKWKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
}

@instance(PairKW::class)
interface PairKWMonadInstance : Monad<PairKWKindPartial<*>> {
    override fun <A, B> map(fa: PairKWKind<*, A>, f: (A) -> B) = fa.ev().map(f)
    override fun <A, B> ap(fa: PairKWKind<*, A>, ff: PairKWKind<*, (A) -> B>) = fa.ev().ap(ff.ev())
    override fun <A> pure(a: A) = PairKW.pure(a)
    override fun <A, B> flatMap(fa: PairKWKind<*, A>, f: (A) -> PairKWKind<*, B>) = fa.ev().flatMap { f(it).ev() }
    override tailrec fun <A, B> tailRecM(a: A, f: (A) -> PairKWKind<*, Either<A, B>>): PairKWKind<*, B> {
        val b = f(a).ev().b
        return when (b) {
            is Either.Left -> tailRecM(b.a, f)
            is Either.Right -> PairKW.pure(b.b)
        }
    }
}

@instance(PairKW::class)
interface PairKWFoldableInstance : Foldable<PairKWKindPartial<*>> {
    override fun <A, B> foldL(fa: PairKWKind<*, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldR(fa: PairKWKind<*, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
}

@instance(PairKW::class)
interface PairKWTraverseInstance<F> : Traverse<PairKWKindPartial<F>> {
    override fun <A, B> foldL(fa: PairKWKind<F, A>, b: B, f: (B, A) -> B) = fa.ev().foldL(b, f)
    override fun <A, B> foldR(fa: PairKWKind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) = fa.ev().foldR(lb, f)
    override fun <G, A, B> traverse(fa: PairKWKind<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>) = fa.ev().traverse(f, GA)
}

infix fun <A, B> A.toKW(b: B) = PairKW(this, b)

fun <A> square(a: A) = a toKW a
