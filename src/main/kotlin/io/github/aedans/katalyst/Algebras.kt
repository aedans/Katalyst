package io.github.aedans.katalyst

import kategory.FunctionK
import kategory.HK
import kategory.Nested

typealias Algebra<F, A> = (HK<F, A>) -> A
typealias AlgebraM<M, F, A> = (HK<F, A>) -> HK<M, A>
typealias GAlgebra<W, F, A> = (HK<F, HK<W, A>>) -> A
typealias GAlgebraM<W, M, F, A> = (HK<F, HK<W, A>>) -> HK<M, A>

typealias Coalgebra<F, A> = (A) -> HK<F, A>
typealias CoalgebraM<M, F, A> = (A) -> HK<M, HK<F, A>>
typealias GCoalgebra<N, F, A> = (A) -> HK<F, HK<N, A>>
typealias GCoalgebraM<N, M, F, A> = (A) -> HK<M, HK<F, HK<N, A>>>

typealias DistributiveLaw<F, G> = FunctionK<Nested<F, G>, Nested<G, F>>
