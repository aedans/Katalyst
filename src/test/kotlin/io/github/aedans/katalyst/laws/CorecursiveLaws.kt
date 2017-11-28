package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.typeclasses.Corecursive
import io.github.aedans.katalyst.typeclasses.corecursive
import kategory.Law

object CorecursiveLaws {
    inline fun <reified T> laws(
            CT: Corecursive<T> = corecursive()
    ): List<Law> = listOf(
    )
}
