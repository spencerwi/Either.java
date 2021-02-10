Either.java
===========

![Java CI with Maven](https://github.com/spencerwi/Either.java/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

A right-biased implementation of Haskell's `Either a b` for Java, using Java 8 for mapping/folding and type inference. 

An `Either<A,B>` is used to "condense" two distinct options down to a single choice.

"Right-biased" means that, like Haskell's `Either a b`, when an `Either<A, B>` has both options available, it prefers
the one on the "right" (that is, `B`). The mnemonic often used in Haskell to remember/explain this "bias" is that an 
Either (usually used for error-checking), gives you *either* the *right* answer, or else whatever's *left*.

Wait, what?
------------

Yes, polymorphism is great in Java. `Animal.speak()`, `Dog.speak()`, `Cat.speak()`, and so on.

But sometimes you want to be REALLY EXPLICIT about the possible types of objects you're dealing with, either (ha ha)
 because they don't logically make sense in the same inheritance structure or because you want to limit your
 possible cases to exactly two classes.

For example, let's say I'm dealing with my code and some third-party library that still uses `java.util.Date` (a class 
that has been largely deprecated since JDK 1.1, and is almost always a strictly-worse choice than using either the newer
`java.time` apis, or Joda Time) . 

Terrible, I know, but there's always *horrible legacy code* out there in the world. 

I don't want to further propagate this old usage of a terrible deprecated class, so I, a Modern Java Developer, prefer 
to use `java.time.LocalDateTime` in my code.


Except great, now I've gotta deal with grabbing a `List<LocalDateTime>` from *these* and a `List<Date>` from *those* and
then do a bunch of gnarly conversion everywhere.

Using `Either.java`, I can instead easily build a `List<Either<Date,LocalDatetime>>`. This tells me (and the compiler) 
that I'm dealing with a bunch of things that are *either* a Date (yuck) or a LocalDateTime.

I can even nicely convert them all into LocalDateTimes:

```java

List<Either<Date, LocalDateTime>> birthdays = allThePeople.stream()
                                                          .map(person -> Either.<Date,LocalDateTime>either(this::getDeprecatedDOBFromPerson, this::getNiceNewDOBFromPersonIfAvailable)
                                                          .collect(Collectors.toList());

List<LocalDateTime> convertedBirthdays = birthdays.stream()
                             .map(eitherDateOrLocalDateTime.fold(
                                (Date deprecatedDOB)   -> myFunctionToConvertDateToLocalDateTime(deprecatedDOB),
                                (LocalDatetime newDOB) -> newDOB
                             )).collect(Collectors.toList());
```

Boom. Now you have a list of LocalDateTimes, and it's explicit from the code that some of the people had deprecated
 Dates, while others had nice, shiny new LocalDateTimes. Even the compiler can tell!


Other common use-cases for an `Either` include capturing and handling errors gracefully using 
`Either<SomeKindOfException, SuccessfulResultClass>`. As mentioned earlier, the convention in the Haskell world (from 
which I totally "borrowed" the Either) is that an Either gives you "either the Right answer or whatever's Left" -- that 
is, errors on the left, expected output on the right.

For this reason, this Either is right-biased; if you give it `Either.either(()->42, ()->"Hello, World!");`, you'll get a
`Right` containing `"Hello, World!"`, not a `Left` containing `42`. I swear, it's not a political thing; there just 
needs to be a predictable rule-of-thumb for how to handle it when the Either gets *both* a left value *and* a right 
value (after all, it's called an *Either*, not a *Both*).

`Result`
------

Because this errors-to-the-left, results-to-the-right idiom is so common, this library also includes a `Result<T>` class, 
which instead of being `Left<L,R>` or `Right<L,R>` is `Err<T>` or `Ok<T>`. Instead of `Either.either(() -> "left",  () -> 42)`,
the constructor method you'll want is `Result.attempt(() -> someMethodThatMightThrowAnException())`. You can even chain
a series of possibly-failing functions using `map`:

```java
Result<C> = Result.attempt(() -> someOperationThatMightFailOrReturnA())
                  .map(a -> someOtherOperationThatMightFailOReturnB(a))
                  .map(b -> someThirdOperationThatMightFailOrReturnC(b));
```

`EitherCollectors`
----------------

If you're working with *collections* of Eithers (for example, you're performing a series of validations, all of which can
*either* succeed *or* return a validation error message), you'll probably be interested in `EitherCollectors`, with its
`.toLeftBiased()` and `.toRightBiased()` methods for use with Java Streams:

```
// Let's say you have this variable already defined from your validations:
// List<Either<ValidationError, ValidationSuccess>> validationResults 

// And you want to fail the user's request if there's *any* error, and return *all* the errors if so. EitherCollectors can help!
Either<List<ValidationError>, List<ValidationSuccess>> errorsOrSuccesses = validationResults.stream()
																				.collect(EitherCollectors.toLeftBiased());

// If there's even *one* left-hand ValidationError in our list, then we'll get a Left containing *all* the ValiidationErrors.
// If not, we'll get a Right containing any ValidationSuccess results.

// So we can do this:
return errorsOrSuccesses.fold(
	(List<ValidationError> errors) -> new UserFacingErrorsResponse(errors),
	(List<ValidationSuccess successes) -> new SuccessResponse(successes)
);
```

So what else can it do?
-----------------------

Wanna see more? Check out the unit tests for a run-down of how Eithers behave. If those tests aren't descriptive enough,
 or you think they should behave differently, open a Github issue! I built this because it didn't look like anyone else
 had built it for Java yet, and I may have lost something in the translation. I'm totally open for feedback.


Cool! How do I get it for my project?
-------------------------------------

Simple! It's in Maven Central, so just add this to your pomfile (or the equivalent for gradle/sbt/lein/whatever):

```
<dependency>
    <groupId>com.spencerwi</groupId>
    <artifactId>Either.java</artifactId>
    <version>2.7.0</version>
</dependency>
```
