package io.github.aedans.katalyst

import arrow.HK
import arrow.core.*
import arrow.free.*
import arrow.typeclasses.Functor

@Suppress("unused")
fun <F, A> Yoneda.Companion.apply(fa: HK<F, A>, FF: Functor<F>) = object : Yoneda<F, A>() {
    override fun <B> invoke(f: (A) -> B) = FF.map(fa, f)
}

fun <T> Either<T, T>.merge() = fold(::identity, ::identity)

fun <S, A, B> Cofree.Companion.unfoldT(
        b: B,
        f: (B) -> Tuple2<A, HK<S, B>>,
        FS: Functor<S>
): Cofree<S, A> = run {
    val (a, sb) = f(b)
    Cofree(FS, a, Eval.later { FS.map(sb) { unfoldT(it, f, FS) } })
}
