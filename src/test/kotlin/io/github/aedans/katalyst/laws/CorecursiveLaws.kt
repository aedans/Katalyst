package io.github.aedans.katalyst.laws

import arrow.test.laws.Law
import io.github.aedans.katalyst.typeclasses.Corecursive

object CorecursiveLaws {
    inline fun <reified T> laws(CT: Corecursive<T>): List<Law> = listOf(

    )
}
