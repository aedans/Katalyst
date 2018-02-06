package io.github.aedans.katalyst.fixedpoint

import arrow.HK
import io.github.aedans.katalyst.data.*

typealias FreePattern<S, A> = CoEnvKindPartial<A, S>

/**
 * Free monad parameterized by a recursive type combinator.
 */
typealias GFree<T, S, A> = HK<T, FreePattern<S, A>>

typealias FixFree<S, A> = GFree<FixHK, S, A>
typealias MuFree<S, A> = GFree<MuHK, S, A>
typealias NuFree<S, A> = GFree<NuHK, S, A>
