package com.spencerwi.either;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * A useful class to wrap operations that may throw exceptions and convert 
 *  exception-handling try-catch code into value-handling code, similar to
 *  "railway-oriented programming". This is especially useful when dealing
 *  with Java Stream methods, which accept the various java.util.function
 *  "functional interfaces", none of which make allowances for checked 
 *  exceptions.
 */
public abstract class Result<R> {

	/**
	 * The "factory function" entry point for using `Result`; effectively acts
	 *  like a try-catch block. If the `resultSupplier` method throws an 
	 *  exception, an `Err` will be returned containing that exception. 
	 *  Otherwise, an `Ok<R>` will be returned containing the return value
	 *  of `resultSupplier`.
	 * @param resultSupplier
	 * @return an `Err<R>` if an exception was thrown; otherwise, an `Ok<R>`
	 */
    public static <R> Result<R> attempt(ExceptionThrowingSupplier<R> resultSupplier){
        try {
            R resultValue = resultSupplier.get();
            return Result.ok(resultValue);
        } catch (Exception e){
            return Result.err(e);
        }
    }

	/**
	 * Factory method for directly creating an `Err<R>` from an exception.
	 * @param e the exception to wrap in an `Err`
	 */
    public static <R> Result<R> err(Exception e){ return new Err<>(e); }
	/**
	 * Factory method for directly creating an `Ok<R>` from a value.
	 * @param result the result value to wrap in an `Ok`
	 */
    public static <R> Result<R> ok(R result){ return new Ok<>(result); }

	/**
	 * @return the wrapped exception if this is an `Err`; otherwise, throws a NoSuchElementException (ironically).
	 * @throws NoSuchElementException if this is an `Ok` 
	 */
    public abstract Exception getException();

	/**
	 * @return the wrapped value if this is an `Ok`; otherwise, throws a NoSuchElementException.
	 * @throws NoSuchElementException if this is an `Err` 
	 */
    public abstract R getResult();

    public abstract boolean isErr();
    public abstract boolean isOk();

	/**
	 * A convenience method for taking an exception and a value and transforming
	 * both to a common type `T`, returning the result of whichever 
	 * transformation is applicable based on whether this is an `Err` or an `Ok`.
	 * @param transformException a method that takes an exception and returns a value of type `T`, useful for handling "fallback" cases.
	 * @param transformValue a method that takes the wrapped value and returns a value of type `T`
	 * @return the return value of `transformValue` if this is an `Ok`, otherwise, the return value of `transformException`
	 */
    public abstract <T> T fold(Function<Exception,T> transformException, Function<R,T> transformValue);
	/**
	 * Applies `transformValue` to the wrapped value and wraps the result in an 
	 * `Ok<T>` for further operations *if* this is an `Ok`; otherwise returns 
	 * the same `Err`, but with the appropriate type so as not to disrupt a 
	 * "chain" of operations.
	 * @param transformValue
	 * @return an `Ok` containing the result of applying transformValue to the wrapped result value if this is an `Ok`, otherwise, the same `Err` but with a friendly type signature.
	 */
    public abstract <T> Result<T> map(ExceptionThrowingFunction<R,T> transformValue);
	/**
	 * Applies a `Result`-returning function to the wrapped value and returns 
	 * that if this is an `Ok<T>`; otherwise returns the same `Err`, but with 
	 * the appropriate type so as not to disrupt a "chain" of operations.
	 * @param transformValue
	 * @return the return value from transformValue if this is an `Ok`, otherwise, the same `Err` but with a friendly type signature.
	 */
    public abstract <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue);

	/**
	 * Runs the acceptsOkValue function if this is an `Ok` instead of an `Err`,
	 * passing in the value that `Ok` wraps; otherwise, does nothing. Does not 
	 * return a value. If you need to transform a `Result<T>`, use {@link #map}
	 * or {@link #flatMap}.
	 */
	public abstract void ifOk(Consumer<R> acceptsOkValue);

	/**
	 * Runs the `errorHandler` function with the wrapped exception if this is an
	 * `Err`, or else runs the `okHandler` function with the wrapped value if 
	 * this is an `Ok`.  Does not return a value. If you need to extract a value 
	 * using a pair of "extractors", see {@link #fold}.
	 */
	public abstract void run(Consumer<Exception> errorHandler, Consumer<R> okHandler);

    public static class Err<R> extends Result<R> {
        private Exception ex;
        private Err(Exception e) {
            this.ex = e;
        }

        @Override
        public Exception getException() { return this.ex; }
        @Override
        public R getResult() { throw new NoSuchElementException("Tried to getResult from an Err"); }

        @Override
        public boolean isErr() { return true; }
        @Override
        public boolean isOk() { return false; }

        @Override
        public <T> T fold(Function<Exception, T> transformException, Function<R, T> transformValue) {
            return transformException.apply(this.ex);
        }

        @Override
        public <T> Result<T> map(ExceptionThrowingFunction<R, T> transformRight) {
            return Result.<T>err(this.ex);
        }
        @Override
        public <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue) {
            return Result.<T>err(this.ex);
        }
		@Override
		public void ifOk(Consumer<R> acceptsOkValue) { /* no-op */ }
		@Override
		public void run(Consumer<Exception> errorHandler, Consumer<R> okHandler) {
			errorHandler.accept(this.ex);
		}

        @Override
        public int hashCode(){ return this.ex.hashCode(); }

		/**
		 * An `Err` object is equal to another object if that other object is
		 *  another `Err` instance containing an exception that is equal to this 
		 *  instance's exception.
		 * */
        @Override
        public boolean equals(Object other){
            if (other instanceof Err<?>){
                final Err<?> otherAsErr = (Err<?>)other;
                return this.ex.equals(otherAsErr.ex);
            } else {
                return false;
            }
        }

    }
    public static class Ok<R> extends Result<R> {
        private R resultValue;
        private Ok(R value) {
            this.resultValue = value;
        }

        @Override
        public Exception getException() { throw new NoSuchElementException("Tried to getException from an Ok"); }
        @Override
        public R getResult() { return resultValue; }

        @Override
        public boolean isErr() { return false; }
        @Override
        public boolean isOk() { return true; }

        @Override
        public <T> T fold(Function<Exception, T> transformException, Function<R, T> transformValue) {
            return transformValue.apply(this.resultValue);
        }
        @Override
        public <T> Result<T> map(ExceptionThrowingFunction<R, T> transformValue) {
            return Result.attempt(() -> transformValue.apply(this.resultValue));
        }
        @Override
        public <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue) {
            try {
                return transformValue.apply(this.resultValue);
            } catch(Exception e) {
                return new Err<T>(e);
            }
        }
		@Override
		public void ifOk(Consumer<R> acceptsOkValue) {
			acceptsOkValue.accept(this.resultValue);
		}
		@Override
		public void run(Consumer<Exception> errorHandler, Consumer<R> okHandler) {
			okHandler.accept(this.resultValue);
		}

        @Override
        public int hashCode(){ return this.resultValue.hashCode(); }
		/**
		 * An `Ok` object is equal to another object if that other object is
		 *  another `Ok` instance containing a value that is equal to this 
		 *  instance's value.
		 * */
        @Override
        public boolean equals(Object other){
            if (other instanceof Ok<?>){
                final Ok<?> otherAsOk = (Ok<?>)other;
                return this.resultValue.equals(otherAsOk.resultValue);
            } else {
                return false;
            }
        }
    }
}
