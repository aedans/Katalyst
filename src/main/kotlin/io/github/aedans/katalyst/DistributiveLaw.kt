package io.github.aedans.katalyst

import arrow.HK
import arrow.core.*
import arrow.typeclasses.*

/**
 * A function from F<G<A>> to G<F<A>> i.e. Traverse.sequence.
 */
interface DistributiveLaw<F, G> : FunctionK<Nested<F, G>, Nested<G, F>> {
    override fun <A> invoke(fa: HK<Nested<F, G>, A>): HK<Nested<G, F>, A> = invokeK(fa.unnest()).nest()
    fun <A> invokeK(fa: HK<F, HK<G, A>>): HK<G, HK<F, A>> = invoke(fa.nest()).unnest()
}

operator fun <F, G, B> DistributiveLaw<F, G>.invoke(p0: HK<F, HK<G, B>>) = invokeK(p0)

fun <F, G> distributiveLaw(TF: Traverse<F>, AG: Applicative<G>): DistributiveLaw<F, G> = object : DistributiveLaw<F, G> {
    override fun <A> invoke(fa: HK<Nested<F, G>, A>) = TF.sequence(AG, fa.unnest()).nest()
}

fun <F> distCata(TF: Traverse<F>): DistributiveLaw<F, IdHK> = object : DistributiveLaw<F, IdHK> {
    override fun <A> invoke(fa: NestedType<F, IdHK, A>) = TF.sequence(Id.applicative(), fa.unnest()).nest()
}
