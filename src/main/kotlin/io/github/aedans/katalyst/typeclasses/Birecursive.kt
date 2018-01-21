package io.github.aedans.katalyst.typeclasses

import arrow.*

@typeclass
interface Birecursive<F> : Recursive<F>, Corecursive<F>, TC
