package io.github.aedans.katalyst

import arrow.Kind
import arrow.core.*
import arrow.free.*
import arrow.typeclasses.Functor

//@Suppress("unused")
//fun <F, A> Yoneda.Companion.apply(fa: Kind<F, A>, FF: Functor<F>) = FF.run {
//    object : Yoneda<F, A>() {
//        override fun <B> invoke(f: (A) -> B) = fa.map { f }
//    }
//}

fun <T> Either<T, T>.merge() = fold(::identity, ::identity)

fun <S, A, B> Cofree.Companion.unfoldT(
        b: B,
        f: (B) -> Tuple2<A, Kind<S, B>>,
        FS: Functor<S>
): Cofree<S, A> = FS.run {
    val (a, sb) = f(b)
    Cofree(FS, a, Eval.later { sb.map { unfoldT(it, f, FS) } })
}
