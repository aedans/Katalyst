package io.github.aedans.katalyst

import io.github.aedans.katalyst.data.NuHK
import io.github.aedans.katalyst.fixedpoint.*
import io.github.aedans.katalyst.typeclasses.birecursive
import io.github.aedans.katalyst.typeclasses.corecursive
import io.github.aedans.katalyst.typeclasses.recursive
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.StringSpec

class NuTest : StringSpec() {
    init {
        "instances can be resolved implicitly" {
            recursive<NuHK>() shouldNotBe null
            corecursive<NuHK>() shouldNotBe null
            birecursive<NuHK>() shouldNotBe null
        }

        "list can be converted to a nu list and back" {
            val list = (0..10).toList()
            list shouldEqual list.nuList.list
        }

        "int can be converted to a nu nat and back" {
            val num = 10
            num shouldEqual num.nuNat.int
        }
    }
}
