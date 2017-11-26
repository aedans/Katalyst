Katalyst
========

[![Download](https://api.bintray.com/packages/aedans/maven/katalyst/images/download.svg)](https://bintray.com/aedans/maven/katalyst/_latestVersion)

 [Kotlin](http://kotlinlang.org) recursion schemes with [Kategory](https://github.com/kategory/kategory).

Gradle
------

```gradle
repositories {
    maven { url 'https://dl.bintray.com/aedans/maven/' }
}

dependencies {
    compile 'io.github.aedans:katalyst:0.2.2'
}
```

Features
--------

- [x] Mu, Nu, and Fix data types
- [x] Recursive, Corecursive, and Birecursive typeclasses
- [x] Cata, ana, and hylo morphisms
- [x] List and Nat defined using recursion schemes
- [x] Kleisli recursion schemes
- [ ] Generalized recursion schemes
- [ ] Elgot recursion schemes
- [ ] Env and CoEnv data types
- [ ] Free and Cofree defined using recursion schemes
- [ ] Recursive and Corecursive instances for Free and Cofree 

Contributing
------------

Katalyst is a long way from being complete, and any help is greatly
appreciated. If you find a bug, please open an issue. If you just want
to write some code, there are plenty of features that need to be implemented;
just open a pull request and hack at it.

Resources
---------

[Recursion Schemes](https://github.com/ekmett/recursion-schemes), the
original Haskell implementation.

[Matryoshka](https://github.com/slamdata/matryoshka), which
much of Katalyst's code is based off of.
