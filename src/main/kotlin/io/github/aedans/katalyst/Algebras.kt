package io.github.aedans.katalyst

import kategory.HK

typealias Algebra<F, A> = (HK<F, A>) -> A
typealias Coalgebra<F, A> = (A) -> HK<F, A>
