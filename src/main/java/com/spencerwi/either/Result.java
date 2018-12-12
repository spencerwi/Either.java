package com.spencerwi.either;

import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class Result<R> {

    public static <R> Result<R> attempt(ExceptionThrowingSupplier<R> resultSupplier){
        try {
            R resultValue = resultSupplier.get();
            return Result.ok(resultValue);
        } catch (Exception e){
            return Result.err(e);
        }
    }

    public static <R> Result<R> err(Exception e){ return new Err<>(e); }
    public static <R> Result<R> ok(R result){ return new Ok<>(result); }

    public abstract Exception getException();
    public abstract R getResult();

    public abstract boolean isErr();
    public abstract boolean isOk();

    public abstract <T> T fold(Function<Exception,T> transformException, Function<R,T> transformValue);
    public abstract <T> Result<T> map(ExceptionThrowingFunction<R,T> transformValue);
    public abstract <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue);

    public static class Err<R> extends Result<R> {
        private Exception leftValue;
        private Err(Exception e) {
            this.leftValue = e;
        }

        @Override
        public Exception getException() { return this.leftValue; }
        @Override
        public R getResult() { throw new NoSuchElementException("Tried to getResult from an Err"); }

        @Override
        public boolean isErr() { return true; }
        @Override
        public boolean isOk() { return false; }

        @Override
        public <T> T fold(Function<Exception, T> transformException, Function<R, T> transformValue) {
            return transformException.apply(this.leftValue);
        }

        @Override
        public <T> Result<T> map(ExceptionThrowingFunction<R, T> transformRight) {
            return Result.<T>err(this.leftValue);
        }
        @Override
        public <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue) {
            return Result.<T>err(this.leftValue);
        }

        @Override
        public int hashCode(){ return this.leftValue.hashCode(); }
        @Override
        public boolean equals(Object other){
            if (other instanceof Err<?>){
                final Err<?> otherAsErr = (Err<?>)other;
                return this.leftValue.equals(otherAsErr.leftValue);
            } else {
                return false;
            }
        }

    }
    public static class Ok<R> extends Result<R> {
        private R rightValue;
        private Ok(R value) {
            this.rightValue = value;
        }

        @Override
        public Exception getException() { throw new NoSuchElementException("Tried to getException from an Ok"); }
        @Override
        public R getResult() { return rightValue; }

        @Override
        public boolean isErr() { return false; }
        @Override
        public boolean isOk() { return true; }

        @Override
        public <T> T fold(Function<Exception, T> transformException, Function<R, T> transformValue) {
            return transformValue.apply(this.rightValue);
        }
        @Override
        public int hashCode(){ return this.rightValue.hashCode(); }
        @Override
        public boolean equals(Object other){
            if (other instanceof Ok<?>){
                final Ok<?> otherAsOk = (Ok<?>)other;
                return this.rightValue.equals(otherAsOk.rightValue);
            } else {
                return false;
            }
        }

        @Override
        public <T> Result<T> map(ExceptionThrowingFunction<R, T> transformValue) {
            return Result.attempt(() -> transformValue.apply(this.rightValue));
        }
        @Override
        public <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue) {
            try {
                return transformValue.apply(this.rightValue);
            } catch(Exception e) {
                return new Err<T>(e);
            }
        }
    }
}
