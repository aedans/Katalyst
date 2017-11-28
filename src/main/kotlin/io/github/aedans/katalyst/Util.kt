package io.github.aedans.katalyst

import kategory.*

@Suppress("unused")
operator fun <F, A> Yoneda.Companion.invoke(fa: HK<F, A>, FF: Functor<F>) = object : Yoneda<F, A>() {
    override fun <B> apply(f: (A) -> B) = FF.map(fa, f)
}

operator fun <F, G, B> FunctionK<Nested<F, G>, Nested<G, F>>.invoke(p0: HK<F, HK<G, B>>) = this(p0.nest()).unnest()

typealias DistributiveLaw<F, G> = FunctionK<Nested<F, G>, Nested<G, F>>
