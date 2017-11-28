package io.github.aedans.katalyst.fixedpoint

import io.github.aedans.katalyst.implicits.ana
import io.github.aedans.katalyst.implicits.cata
import kategory.HK
import kategory.Option.None
import kategory.Option.Some
import kategory.OptionHK
import kategory.OptionKind
import kategory.ev

typealias Nat<T> = HK<T, OptionHK>

inline fun <reified T> Int.nat(): Nat<T> = ana { if (it == 0) None else Some(it - 1) }
inline val <reified T> Nat<T>.int get() = cata { it: OptionKind<Int> -> it.ev().fold({ 0 }, { it + 1 }) }
