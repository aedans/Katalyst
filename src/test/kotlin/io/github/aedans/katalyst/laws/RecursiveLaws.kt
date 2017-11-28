package io.github.aedans.katalyst.laws

import io.github.aedans.katalyst.typeclasses.Recursive
import io.github.aedans.katalyst.typeclasses.recursive
import kategory.Law

object RecursiveLaws {
    inline fun <reified T> laws(
            RT: Recursive<T> = recursive()
    ): List<Law> = listOf(
    )
}
