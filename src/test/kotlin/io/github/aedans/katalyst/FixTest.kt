package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.FixHK
import io.github.aedans.katalyst.fixedpoint.fixList
import io.github.aedans.katalyst.fixedpoint.fixNat
import io.github.aedans.katalyst.fixedpoint.int
import io.github.aedans.katalyst.fixedpoint.list
import io.github.aedans.katalyst.typeclasses.birecursive
import io.github.aedans.katalyst.typeclasses.corecursive
import io.github.aedans.katalyst.typeclasses.recursive
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec

class FixTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            recursive<FixHK>() shouldNotBe null
            corecursive<FixHK>() shouldNotBe null
            birecursive<FixHK>() shouldNotBe null
        }

        "list can be converted to a fix list and back" {
            val list = (0..10).toList()
            list shouldEqual list.fixList.list
        }

        "int can be converted to a fix nat and back" {
            val num = 10
            num shouldEqual num.fixNat.int
        }
    }
}
