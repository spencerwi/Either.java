2.7.0
-----
 * Add `.getLeftOrElseThrow(Function<R, Exception>)` and `.getRightOrElseThrow(Function<L, Exception>)` (thanks, jartysiewicz!)

2.6.0
-----
 * Add `EitherCollectors` with `.toLeftBiased()` and `.toRightBiased()` (thanks, peterunold!)

2.5.0
-----
 * Add `getLeftOrElseThrow` and `getRightOrElseThrow` to Either (thanks, michaldo!)

2.4.0
-----
 * Add `ifOk` and `run` to `Result`
 * Add javadocs to `Result`

2.3.0
-----
 * Add `flatMap` to Either
 * Add javadocs to Either
 * Refactor some tests

2.2.1
-----
 * Drop gradle, switch to maven for builds. Gradle is a hassle.

2.2.0
-----
 * Add `mapLeft`, `mapRight`, `flatMapLeft`, and `flatMapRight` to `Either` (thanks, michaldo!)

2.1.1
-----
 * Upgrade tests to use JUnit 5
 * Include gradle wrapper for more-deterministic builds in CI
 * Ensure CI pipeline builds on jdk8 and jdk11

2.1.0
-----
* Merge @sfesenko's fix to Result's `map` function where it didn't use `attempt` to "wrap" exceptions.

2.0.0
-----

* Fixed type signature of Result's `flatMap` function (should be accept an `A -> Result<B>` and return a `Result<B>`)

1.2.1
-----

* Removed duplication (`Left`s don't need the `rightValue` field, and vice-versa)

1.2.0
-----

* Add `.run(leftConsumer, rightConsumer)` to allow "terminal" operations on an Either.
