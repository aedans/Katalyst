package io.github.aedans.katalyst.laws

import arrow.test.laws.Law
import io.github.aedans.katalyst.typeclasses.Recursive

object RecursiveLaws {
    inline fun <reified T> laws(RT: Recursive<T>): List<Law> = listOf(

    )
}
