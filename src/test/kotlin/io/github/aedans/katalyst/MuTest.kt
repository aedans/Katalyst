package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.MuHK
import io.github.aedans.katalyst.data.NuHK
import io.github.aedans.katalyst.fixedpoint.int
import io.github.aedans.katalyst.fixedpoint.list
import io.github.aedans.katalyst.fixedpoint.muList
import io.github.aedans.katalyst.fixedpoint.muNat
import io.github.aedans.katalyst.typeclasses.birecursive
import io.github.aedans.katalyst.typeclasses.corecursive
import io.github.aedans.katalyst.typeclasses.recursive
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec

class MuTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            recursive<MuHK>() shouldNotBe null
            corecursive<MuHK>() shouldNotBe null
            birecursive<NuHK>() shouldNotBe null
        }

        "list can be converted to a mu list and back" {
            val list = (0..10).toList()
            list shouldEqual list.muList.list
        }

        "int can be converted to a mu nat and back" {
            val num = 10
            num shouldEqual num.muNat.int
        }
    }
}
