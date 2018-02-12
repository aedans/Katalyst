package io.github.aedans.katalyst

import arrow.HK
import arrow.core.*
import arrow.free.*
import arrow.typeclasses.*
import io.github.aedans.katalyst.data.*
import io.github.aedans.katalyst.fixedpoint.toGFree
import io.github.aedans.katalyst.syntax.cata

/**
 * A function from F<G<A>> to G<F<A>> i.e. Traverse.sequence.
 */
interface DistributiveLaw<F, G> : FunctionK<Nested<F, G>, Nested<G, F>> {
    override fun <A> invoke(fa: HK<Nested<F, G>, A>): HK<Nested<G, F>, A> = invokeK(fa.unnest()).nest()
    fun <A> invokeK(fa: HK<F, HK<G, A>>): HK<G, HK<F, A>> = invoke(fa.nest()).unnest()

    companion object {
        fun <T> refl() = object : DistributiveLaw<T, T> {
            override fun <A> invokeK(fa: HK<T, HK<T, A>>) = fa
        }
    }
}

operator fun <F, G, B> DistributiveLaw<F, G>.invoke(p0: HK<F, HK<G, B>>) = invokeK(p0)

fun <F, G> distributiveLaw(TF: Traverse<F>, AG: Applicative<G>) = object : DistributiveLaw<F, G> {
    override fun <A> invoke(fa: HK<Nested<F, G>, A>) = TF.sequence(AG, fa.unnest()).nest()
}

fun <F> distCata(TF: Traverse<F>) = object : DistributiveLaw<F, IdHK> {
    override fun <A> invoke(fa: NestedType<F, IdHK, A>) = TF.sequence(Id.applicative(), fa.unnest()).nest()
}

fun <F> distHisto(FF: Functor<F>) =
        object : DistributiveLaw<F, CofreeKindPartial<F>> by distGHisto<F, F>(DistributiveLaw.refl(), FF, FF) { }

fun <F, H> distGHisto(
        dFH: DistributiveLaw<F, H>,
        FF: Functor<F>,
        FH: Functor<H>
) = object : DistributiveLaw<F, CofreeKindPartial<H>> {
    override fun <A> invokeK(fa: HK<F, CofreeKind<H, A>>) = Cofree.unfoldT(
            fa,
            {
                Tuple2(
                        FF.map(it) { it.ev().extract() },
                        dFH.invokeK(FF.map(it) { it.ev().tailForced() })
                )
            },
            FH)
}

fun <F> distFutu(FF: Functor<F>) =
        object : DistributiveLaw<FreeKindPartial<F>, F> by distGFutu<F, F>(DistributiveLaw.refl(), FF, FF) { }

fun <F, H> distGFutu(dHF: DistributiveLaw<H, F>, FF: Functor<F>, FH: Functor<H>) =
        object : DistributiveLaw<FreeKindPartial<H>, F> {
            override fun <A> invokeK(fa: FreeKind<H, HK<F, A>>): HK<F, FreeKind<H, A>> =
                    fa.ev().toGFree<MuHK, H, HK<F, A>>(FH).cata {
                        it.ev().run.fold(
                                { FF.map(it) { Free.pure<H, A>(it) } },
                                { FF.map(dHF.invokeK(it)) { Free.monad<H>().flatten(Free.liftF(it)) } }
                        )
                    }
        }
