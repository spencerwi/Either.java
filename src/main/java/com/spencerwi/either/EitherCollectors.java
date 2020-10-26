package com.spencerwi.either;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * These collectors collect a stream of Either<L,R> objects to an Either<List<L>, List<R>> object.
 *
 * @param <L> the "left side" type.
 * @param <R> the "right side type.
 */

public class EitherCollectors<L,R> implements Collector< Either<L,R>, EitherCollectors.EitherAccumulator<L,R>, Either<List<L>, List<R>> > {
    private final boolean leftBiased;

    /**
     * Factory method for creating a left biased collector which produces an Either<List<L>, List<R>> object,
     * where the list contains all the left or right Either values of the stream.
     * Since this is a left biased collector, the resulting Either is left iff the stream is empty or contains
     * at least one left Either object.
     * @return Either<List<L>, List<R>>
     */
    public static <L,R> Collector<Either<L,R>, ?, Either<List<L>, List<R>>> toLeftBiased() {
        return new EitherCollectors<>(true);
    }

    /**
     * Factory method for creating a right biased collector which produces an Either<List<L>, List<R>> object,
     * where the list contains all the left or right Either values of the stream.
     * Since this is a right biased collector, the resulting Either is right iff the stream is empty or contains
     * at least one right Either object.
     * @return Either<List<L>, List<R>>
     */
    public static <L,R> Collector<Either<L,R>, ?, Either<List<L>, List<R>>> toRightBiased() {
        return new EitherCollectors<>(false);
    }

    private EitherCollectors(boolean leftBiased) {
        this.leftBiased = leftBiased;
    }

    @Override
    public Supplier<EitherAccumulator<L, R>> supplier() {
        return () -> new EitherAccumulator<>(leftBiased);
    }

    @Override
    public BiConsumer<EitherAccumulator<L, R>, Either<L, R>> accumulator() {
        return EitherAccumulator::add;
    }

    @Override
    public BinaryOperator<EitherAccumulator<L, R>> combiner() {
        return EitherAccumulator::append;
    }

    @Override
    public Function<EitherAccumulator<L, R>, Either<List<L>, List<R>>> finisher() {
        return EitherAccumulator::finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
    }

    static class EitherAccumulator<L,R> {
        private final List<L> lefts;
        private final List<R> rights;
        private final boolean leftBiased;

        EitherAccumulator(boolean leftBiased) {
            this.leftBiased = leftBiased;
            this.lefts = new ArrayList<>();
            this.rights = new ArrayList<>();
        }

        void add(Either<L,R> e) {
            e.run(lefts::add, rights::add);
        }

        EitherAccumulator<L,R> append(EitherAccumulator<L,R> accumulator2) {
            lefts.addAll(accumulator2.lefts);
            rights.addAll(accumulator2.rights);
            return this;
        }

        Either<List<L>, List<R>> finisher() {
            if(leftBiased) {
                return !lefts.isEmpty() || rights.isEmpty()  ? Either.left(lefts) : Either.right(rights);
            } else {
                return !rights.isEmpty() || lefts.isEmpty() ? Either.right(rights) : Either.left(lefts);
            }
        }
    }

}
