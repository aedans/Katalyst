package io.github.aedans.katalyst.typeclasses

import kategory.InstanceParametrizedType
import kategory.Typeclass
import kategory.instance
import kategory.typeLiteral

interface Birecursive<F> : Recursive<F>, Corecursive<F>, Typeclass

inline fun <reified F> birecursive(): Birecursive<F> = instance(InstanceParametrizedType(Birecursive::class.java, listOf(typeLiteral<F>())))
