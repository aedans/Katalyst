package io.github.aedans.katalyst

import arrow.Kind
import arrow.core.*
import arrow.typeclasses.*

/**
 * A function from F<G<A>> to G<F<A>> i.e. Traverse.sequence.
 */
interface DistributiveLaw<F, G> : FunctionK<Nested<F, G>, Nested<G, F>> {
    override fun <A> invoke(fa: Kind<Nested<F, G>, A>): Kind<Nested<G, F>, A> = invokeK(fa.unnest()).nest()
    fun <A> invokeK(fa: Kind<F, Kind<G, A>>): Kind<G, Kind<F, A>> = invoke(fa.nest()).unnest()

    companion object {
        fun <T> refl() = object : DistributiveLaw<T, T> {
            override fun <A> invokeK(fa: Kind<T, Kind<T, A>>) = fa
        }
    }
}

operator fun <F, G, B> DistributiveLaw<F, G>.invoke(p0: Kind<F, Kind<G, B>>) = invokeK(p0)

fun <F, G> distributiveLaw(TF: Traverse<F>, AG: Applicative<G>) = TF.run {
    object : DistributiveLaw<F, G> {
        override fun <A> invoke(fa: Kind<Nested<F, G>, A>) = fa.unnest().sequence(AG).nest()
    }
}

fun <F> distCata(TF: Traverse<F>) = TF.run {
    object : DistributiveLaw<F, ForId> {
        override fun <A> invoke(fa: NestedType<F, ForId, A>) = fa.unnest().sequence(Id.applicative()).nest()
    }
}
