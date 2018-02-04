package io.github.aedans.katalyst.typeclasses

import arrow.*

/**
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
@typeclass
interface Birecursive<F> : Recursive<F>, Corecursive<F>, TC
