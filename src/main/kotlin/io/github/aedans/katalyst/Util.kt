package io.github.aedans.katalyst

import arrow.HK
import arrow.core.*
import arrow.free.Yoneda
import arrow.typeclasses.*

@Suppress("unused")
fun <F, A> Yoneda.Companion.apply(fa: HK<F, A>, FF: Functor<F>) = object : Yoneda<F, A>() {
    override fun <B> invoke(f: (A) -> B) = FF.map(fa, f)
}

operator fun <F, G, B> FunctionK<Nested<F, G>, Nested<G, F>>.invoke(p0: HK<F, HK<G, B>>) = this(p0.nest()).unnest()

typealias DistributiveLaw<F, G> = FunctionK<Nested<F, G>, Nested<G, F>>

fun <T> Either<T, T>.merge() = fold(::identity, ::identity)
