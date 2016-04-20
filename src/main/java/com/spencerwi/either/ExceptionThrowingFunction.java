package com.spencerwi.either;

import java.lang.FunctionalInterface;

@FunctionalInterface
public interface ExceptionThrowingFunction<T,R> {
    R apply(T t) throws Exception;
}
