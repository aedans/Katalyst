package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.data.FixHK
import io.github.aedans.katalyst.data.MuHK
import io.github.aedans.katalyst.data.NuHK
import io.github.aedans.katalyst.typeclasses.ana
import io.github.aedans.katalyst.typeclasses.cata
import kategory.*

@higherkind
sealed class ListF<out F, out A> : ListFKind<F, A> {
    companion object
}

data class Cons<out F, out A>(val head: F, val tail: A) : ListF<F, A>()
object Nil : ListF<Nothing, Nothing>()

val <F, A> ListF<F, A>.either get() = when (this) {
    is Nil -> this.left()
    is Cons -> this.right()
}

@instance(ListF::class)
interface ListFFunctorInstance<F> : Functor<ListFKindPartial<F>> {
    override fun <A, B> map(fa: ListFKind<F, A>, f: (A) -> B) = fa.ev().either.fold({ Nil }, { Cons(it.head, f(it.tail)) })
}

typealias RList<T, A> = HK<T, ListFKindPartial<A>>

inline fun <reified T, A> List<A>.rList(): RList<T, A> = ana { if (it.isEmpty()) Nil else Cons(it.first(), it.drop(1)) }

inline val <reified T, A> RList<T, A>.list get(): List<A> =
    cata { it.ev().either.fold({ emptyList() }, { listOf(it.head) + it.tail }) }

val <A> List<A>.fixList: RList<FixHK, A> get() = rList()
val <A> List<A>.muList: RList<MuHK, A> get() = rList()
val <A> List<A>.nuList: RList<NuHK, A> get() = rList()
