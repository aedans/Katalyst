package io.github.aedans.katalyst

import kategory.HK

@Suppress("FunctionName")
object Algebras {
    @JvmStatic fun <F, A> Algebra(it: Algebra<F, A>) = it
    @JvmStatic fun <M, F, A> AlgebraM(it: AlgebraM<M, F, A>) = it
    @JvmStatic fun <N, F, A> GAlgebra(it: GAlgebra<N, F, A>) = it
    @JvmStatic fun <N, M, F, A> GAlgebraM(it: GAlgebraM<N, M, F, A>) = it

    @JvmStatic fun <F, A> Coalgebra(it: Coalgebra<F, A>) = it
    @JvmStatic fun <M, F, A> CoalgebraM(it: CoalgebraM<M, F, A>) = it
    @JvmStatic fun <N, F, A> GCoalgebra(it: GCoalgebra<N, F, A>) = it
    @JvmStatic fun <N, M, F, A> GCoalgebraM(it: GCoalgebraM<N, M, F, A>) = it
}

typealias Algebra<F, A> = (HK<F, A>) -> A
typealias AlgebraM<M, F, A> = (HK<F, A>) -> HK<M, A>
typealias GAlgebra<W, F, A> = (HK<F, HK<W, A>>) -> A
typealias GAlgebraM<W, M, F, A> = (HK<F, HK<W, A>>) -> HK<M, A>

typealias Coalgebra<F, A> = (A) -> HK<F, A>
typealias CoalgebraM<M, F, A> = (A) -> HK<M, HK<F, A>>
typealias GCoalgebra<N, F, A> = (A) -> HK<F, HK<N, A>>
typealias GCoalgebraM<N, M, F, A> = (A) -> HK<M, HK<F, HK<N, A>>>
