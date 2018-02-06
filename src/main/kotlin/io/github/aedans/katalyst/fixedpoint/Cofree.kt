package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import io.github.aedans.katalyst.data.*

typealias CofreePattern<S, A> = EnvTKindPartial<A, S>

/**
 * Cofree comonad parameterized by a recursive type combinator.
 */
typealias GCofree<T, S, A> = HK<T, CofreePattern<A, S>>

typealias FixCofree<S, A> = GCofree<FixHK, S, A>
typealias MuCofree<S, A> = GCofree<MuHK, S, A>
typealias NuCofree<S, A> = GCofree<NuHK, S, A>
