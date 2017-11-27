package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.data.FixHK
import io.github.aedans.katalyst.data.MuHK
import io.github.aedans.katalyst.data.NuHK
import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.cata
import kategory.*

@higherkind
sealed class ListF<out F, out A> : ListFKind<F, A> {
    data class Cons<out F, out A>(val head: F, val tail: A) : ListF<F, A>()
    object Nil : ListF<Nothing, Nothing>()

    companion object
}

@instance(ListF::class)
interface ListFFunctorInstance<F> : Functor<ListFKindPartial<F>> {
    override fun <A, B> map(fa: ListFKind<F, A>, f: (A) -> B) =
            fa.ev().either.fold({ ListF.Nil }, { ListF.Cons(it.head, f(it.tail)) })
}

val <F, A> ListF<F, A>.either get() = when (this) {
    is ListF.Nil -> this.left()
    is ListF.Cons -> this.right()
}

typealias RList<T, A> = HK<T, ListFKindPartial<A>>

inline fun <reified T, A> List<A>.rList(): RList<T, A> = ana { if (it.isEmpty()) ListF.Nil else ListF.Cons(it.first(), it.drop(1)) }

inline val <reified T, A> RList<T, A>.list get(): List<A> =
    cata { it.ev().either.fold({ emptyList() }, { listOf(it.head) + it.tail }) }

val <A> List<A>.fixList: RList<FixHK, A> get() = rList()
val <A> List<A>.muList: RList<MuHK, A> get() = rList()
val <A> List<A>.nuList: RList<NuHK, A> get() = rList()
