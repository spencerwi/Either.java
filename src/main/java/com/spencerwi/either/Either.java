package com.spencerwi.either;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
* A class that can be used to handle situations where one of two disjoint types
*  may be assigned to a value or returned from a method, allowing for 
*  straightforward transformations and eventual "reduction" to a single, 
*  commonly-typed value. This class is "right-biased", meaning that in 
*  situations where both a "left side" and a "right side" are provided, the 
*  "right side" is preferred. This follows the "you either get the right answer
*  or whatever's left" mnemonic used in Haskell, where this concept is commonly
*  used.
*
* Two Either instances are equal if they are both the same "side" and both contain
*  equal values. See {@link Either.Left#equals} and {@link Either.Right#equals}.
* @param <L> the "left side" type.
* @param <R> the "right side type.
 */
public abstract class Either<L, R> {

	/**
	 * Factory method for creating an Either instance from a left-supplier and a
	 *  right-supplier; if both are provided, the right one is preferred.
	 * @param leftSupplier 
	 * @param rightSupplier
	 * @return either a Left or a Right instance, depending on which values are available.
	 */
    public static <L,R> Either<L,R> either(Supplier<L> leftSupplier, Supplier<R> rightSupplier){
        R rightValue = rightSupplier.get();
        if (rightValue != null){
            return Either.<L,R>right(rightValue);
        } else {
            return Either.<L,R>left(leftSupplier.get());
        }
    }

	/**
	 * Quick factory method for directly creating a Left.
	 * @param left the left value to wrap.
	 * @return the resulting Left.
	 */
    public static <L,R> Either<L,R> left(L left){ return new Left<>(left); }
	/**
	 * Quick factory method for directly creating a Right.
	 * @param right the right value to wrap.
	 * @return the resulting Right.
	 */
    public static <L,R> Either<L,R> right(R right){ return new Right<>(right); }

	/**
	 * Forcibly gets the left-wrapped value if this is a Left, or throws a
	 *  {@link NoSuchElementException} if this is a Right.
	 * @throws NoSuchElementException if this is a Right.
	 * @return the contents of the Left if this is a Left.
	 */
    public abstract L getLeft();
	/**
	 * Forcibly gets the right-wrapped value if this is a Right, or throws a
	 *  {@link NoSuchElementException} if this is a Left.
	 * @throws NoSuchElementException if this is a Left.
	 * @return the contents of the Right if this is a Right.
	 */
    public abstract R getRight();

    public abstract boolean isLeft();
    public abstract boolean isRight();

	/**
	 * Attempts to apply a common-target-type transformation to the right side 
	 *  value if this is a Right, or else the left side value if this is a Left,
	 *  and returns the result.
	 * @param transformLeft the transformation to apply to the left side value if this is a Left.
	 * @param transformRight the transformation to apply to the right side value if this is a Right.
	 * @return the result of applying whichever transformation is relevant for this instance.
	 */
    public abstract <T> T fold(Function<L,T> transformLeft, Function<R,T> transformRight);

	/**
	 * Allows a transformation to be applied to both sides, resulting in a new 
	 *  Either instance containing the return value of the relevant 
	 *  transformation.
	 * @param transformLeft the transformation to apply to the left side value if this is a Left.
	 * @param transformRight the transformation to apply to the right side value if this is a Right.
	 * @return a new Either wrapping the transformed left or right value.
	 */
    public abstract <T,U> Either<T,U> map(Function<L,T> transformLeft, Function<R,U> transformRight);

	/**
	 * Applies an either-returning transformation to the Right-side value if this
	 *  is a Right, and effectively returns the resulting Either to avoid 
	 *  unnecessary nesting; otherwise, it does the same, but on the left side.
	 * @param eitherTransformLeft the either-returning transformation to apply to the left side value if this is a Left, which will be unwrapped from the "Either" that's returned.
	 * @param eitherTransformRight the either-returning transformation to apply to the right side value if this is a Right, which will be unwrapped from the "Either" that's returned.
	 * @return basically the return value of whichever side ran.
	 */
	public abstract <T,U> Either<T,U> flatMap(Function<L, Either<T,U>> eitherTransformLeft, Function<R, Either<T,U>> eitherTransformRight);

	/**
	 * Applies a `Consumer<R>` to the right side if this is a Right, otherwise
	 *  applies a `Consumer<L>` to the left side if this is a Left.
	 * @param runLeft the consumer to apply to the left-wrapped value if this is a Left.
	 * @param runRight the consumer to apply to the right-wrapped value if this is a Right.
	 */
    public abstract void run(Consumer<L> runLeft, Consumer<R> runRight);

	/**
	 * Applies a transformation to the Left-side value if this is a Left and
	 *  returns a correctly-typed Either; otherwise, just returns a 
	 *  correctly-typed Either wrapping the same Right value. This is a 
	 *  convenience wrapper around running {@link #map} and just passing
	 *  {@link java.util.Function.identity} for the right side.
	 * @param transformLeft the transformation to apply to the left side value if this is a Left.
	 * @return a correctly-typed Either based on the transformation's type.
	 */
    public <L2> Either<L2,R> mapLeft(Function<L,L2> transformLeft) {
        return map(transformLeft, Function.identity());
    }

	/**
	 * Applies a transformation to the Right-side value if this is a Right and
	 *  returns a correctly-typed Either; otherwise, just returns a 
	 *  correctly-typed Either wrapping the same Right value. This is a 
	 *  convenience wrapper around running {@link #map} and just passing
	 *  {@link java.util.Function.identity} for the right side.
	 * @param transformRight the transformation to apply to the right side value if this is a Right.
	 * @return a correctly-typed Either based on the transformation's type.
	 */
    public <R2> Either<L,R2> mapRight(Function<R,R2> transformRight) {
        return map(Function.identity(), transformRight);
    }

	/**
	 * Applies an either-returning transformation to the Left-side value if this
	 *  is a Left, and effectively returns the resulting Either to avoid 
	 *  unnecessary nesting; otherwise, it returns the existing Right but typed
	 *  correctly. This is a convenience wrapper around running {@link #flatMap}
	 *  and just passing {@link java.util.Function.identity} for the right side.
	 * @param transformLeft an either-returning transformation to apply to the left side value if this is a Left.
	 * @return a correctly-typed Either based on the transformation's type.
	 */
    public abstract <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2,R>> transformLeft);

	/**
	 * Applies an either-returning transformation to the Right-side value if this
	 *  is a Right, and effectively returns the resulting Either to avoid 
	 *  unnecessary nesting; otherwise, it returns the existing Left but typed
	 *  correctly. This is a convenience wrapper around running 
	 *  {@link #flatMap} and just passing {@link java.util.Function.identity} 
	 *  for the right side.
	 * @param transformLeft an either-returning transformation to apply to the left side value if this is a Left.
	 * @return a correctly-typed Either based on the transformation's type.
	 */
    public abstract <R2> Either<L, R2> flatMapRight(Function<R, Either<L,R2>> transformRight);

	/**
	 * Returns the left-side value if this is a Left; otherwise throws the 
	 *  exception supplied by `exceptionSupplier`.
	 * @param exceptionSupplier a Supplier that returns a Throwable that will be thrown if this is a Right.
	 * @return the left-side value if this is a Left.
	 * @throws X if this is a Right.
	 */
    public abstract <X extends Throwable> L getLeftOrElseThrow(Supplier<X> exceptionSupplier) throws X;

	/**
	 * Returns the left-side value if this is a Left; otherwise throws the
	 *  exception which is a result of transforming Right by `rightToException`.
	 * @param rightToException a Function that gets Right and returns a Throwable that will be thrown if this is a Right.
	 * @return the left-side value if this is a Left.
	 * @throws X if this is a Right.
	 */
	public abstract <X extends Throwable> L getLeftOrElseThrow(Function<R, X> rightToException) throws X;

	/**
	 * Returns the right-side value if this is a Right; otherwise throws the 
	 *  exception supplied by `exceptionSupplier`.
	 * @param exceptionSupplier a Supplier that returns a Throwable that will be thrown if this is a Left.
	 * @return the right-side value if this is a Right.
	 * @throws X if this is a Left.
	 */
    public abstract <X extends Throwable> R getRightOrElseThrow(Supplier<X> exceptionSupplier) throws X;

	/**
	 * Returns the right-side value if this is a Right; otherwise throws the
	 *  exception which is a result of transforming Left by `leftToException`.
	 * @param leftToException a Function that gets Left and returns a Throwable that will be thrown if this is a Left.
	 * @return the right-side value if this is a Right.
	 * @throws X if this is a Left.
	 */
	public abstract <X extends Throwable> R getRightOrElseThrow(Function<L,X> leftToException) throws X;

    public static class Left<L,R> extends Either<L, R> {

        protected L leftValue;

        private Left(L left) {
            this.leftValue = left;
        }

        @Override
        public L getLeft() { return this.leftValue; }
        @Override
        public R getRight() { throw new NoSuchElementException("Tried to getRight from a Left"); }

        @Override
        public boolean isLeft() { return true; }
        @Override
        public boolean isRight() { return false; }

        @Override
        public <T> T fold(Function<L, T> transformLeft, Function<R, T> transformRight) {
            return transformLeft.apply(this.leftValue);
        }

        @Override
        public <T, U> Either<T, U> map(Function<L, T> transformLeft, Function<R, U> transformRight) {
            return Either.<T,U>left(transformLeft.apply(this.leftValue));
        }

		@Override
		public <T, U> Either<T, U> flatMap(
				Function<L, Either<T,U>> eitherTransformLeft, 
				Function<R, Either<T,U>> eitherTransformRight
		) {
			return eitherTransformLeft.apply(this.leftValue);
		}

        @Override
        public void run(Consumer<L> runLeft, Consumer<R> runRight) {
            runLeft.accept(this.leftValue);
        }

        @Override
        public <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2,R>> transformLeft) {
            return transformLeft.apply(leftValue);
        }

        @Override
        public <R2> Either<L, R2> flatMapRight(Function<R, Either<L,R2>> transformRight) {
            return Either.left(leftValue);
        }

        @Override
        public <X extends Throwable> L getLeftOrElseThrow(Supplier<X> exceptionSupplier) throws X {
            return leftValue;
        }

		@Override
		public <X extends Throwable> L getLeftOrElseThrow(Function<R, X> rightToException) throws X {
			return leftValue;
		}

		@Override
        public <X extends Throwable> R getRightOrElseThrow(Supplier<X> exceptionSupplier) throws X {
            throw exceptionSupplier.get();
        }

		@Override
		public <X extends Throwable> R getRightOrElseThrow(Function<L, X> leftToException) throws X {
			throw leftToException.apply(leftValue);
		}


		@Override
        public int hashCode(){ return this.leftValue.hashCode(); }

		/**
		 * A Left is equal to another object *if* the other object is a Left
		 *  which wraps a value equal to the value wrapped by this one.
		 */
        @Override
        public boolean equals(Object other){
            if (other instanceof Left<?,?>){
                final Left<?, ?> otherAsLeft = (Left<?, ?>)other;
                return this.leftValue.equals(otherAsLeft.leftValue);
            } else {
                return false;
            }
        }

    }
    public static class Right<L,R> extends Either<L, R> {

        protected R rightValue;

        private Right(R right) {
            this.rightValue = right;
        }

        @Override
        public L getLeft() { throw new NoSuchElementException("Tried to getLeft from a Right"); }
        @Override
        public R getRight() { return rightValue; }

        @Override
        public boolean isLeft() { return false; }
        @Override
        public boolean isRight() { return true; }

        @Override
        public <T> T fold(Function<L, T> transformLeft, Function<R, T> transformRight) {
            return transformRight.apply(this.rightValue);
        }

        @Override
        public <T, U> Either<T, U> map(Function<L, T> transformLeft, Function<R, U> transformRight) {
            return Either.<T,U>right(transformRight.apply(this.rightValue));
        }
		
		@Override
		public <T, U> Either<T, U> flatMap(
				Function<L, Either<T,U>> eitherTransformLeft, 
				Function<R, Either<T,U>> eitherTransformRight
		) {
			return eitherTransformRight.apply(this.rightValue);
		}


        @Override
        public void run(Consumer<L> runLeft, Consumer<R> runRight) {
            runRight.accept(this.rightValue);
        }

        @Override
        public <L2> Either<L2, R> flatMapLeft(Function<L, Either<L2,R>> transformLeft) {
            return Either.right(rightValue);
        }

        @Override
        public <R2> Either<L, R2> flatMapRight(Function<R, Either<L, R2>> transformRight) {
            return transformRight.apply(rightValue);
        }

        @Override
        public <X extends Throwable> L getLeftOrElseThrow(Supplier<X> exceptionSupplier) throws X {
            throw exceptionSupplier.get();
        }

		@Override
		public <X extends Throwable> L getLeftOrElseThrow(Function<R, X> rightToException) throws X {
			throw rightToException.apply(rightValue);
		}


		@Override
        public <X extends Throwable> R getRightOrElseThrow(Supplier<X> exceptionSupplier) throws X {
            return rightValue;
        }

		@Override
		public <X extends Throwable> R getRightOrElseThrow(Function<L, X> leftToException) throws X {
			return rightValue;
		}


		@Override
        public int hashCode(){ return this.rightValue.hashCode(); }

		/**
		 * A Right is equal to another object *if* the other object is a Right
		 *  which wraps a value equal to the value wrapped by this one.
		 */
        @Override
        public boolean equals(Object other){
            if (other instanceof Right<?,?>){
                final Right<?, ?> otherAsRight = (Right<?, ?>)other;
                return this.rightValue.equals(otherAsRight.rightValue);
            } else {
                return false;
            }
        }

    }
}
