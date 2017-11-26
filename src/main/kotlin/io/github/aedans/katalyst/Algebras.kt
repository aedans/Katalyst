package io.github.aedans.katalyst

import kategory.HK

typealias Algebra<F, A> = (HK<F, A>) -> A
typealias AlgebraM<M, F, A> = (HK<F, A>) -> HK<M, A>

typealias Coalgebra<F, A> = (A) -> HK<F, A>
typealias CoalgebraM<M, F, A> = (A) -> HK<M, HK<F, A>>
