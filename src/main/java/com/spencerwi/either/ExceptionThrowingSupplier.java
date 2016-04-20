package com.spencerwi.either;

import java.lang.FunctionalInterface;

@FunctionalInterface
public interface ExceptionThrowingSupplier<T> {
    T get() throws Exception;
}

