package io.github.aedans.katalyst

import arrow.HK
import arrow.core.*
import arrow.free.Yoneda
import arrow.typeclasses.Functor

@Suppress("unused")
fun <F, A> Yoneda.Companion.apply(fa: HK<F, A>, FF: Functor<F>) = object : Yoneda<F, A>() {
    override fun <B> invoke(f: (A) -> B) = FF.map(fa, f)
}

fun <T> Either<T, T>.merge() = fold(::identity, ::identity)
