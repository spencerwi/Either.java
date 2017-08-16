2.0.0
-----

* Fixed type signature of Result's `flatMap` function (should be accept an `A -> Result<B>` and return a `Result<B>`)

1.2.1
-----

* Removed duplication (`Left`s don't need the `rightValue` field, and vice-versa)

1.2.0
-----

* Add `.run(leftConsumer, rightConsumer)` to allow "terminal" operations on an Either.
