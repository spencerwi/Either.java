package com.spencerwi.either;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class EitherCollectors<L,R> implements Collector< Either<L,R>, EitherCollectors.EitherAccumulator<L,R>, Either<List<L>, List<R>> > {
    private final boolean leftBiased;

    public static <L,R> Collector<Either<L,R>, ?, Either<List<L>, List<R>>> toLeftBiasedList() {
        return new EitherCollectors<>(true);
    }

    public static <L,R> Collector<Either<L,R>, ?, Either<List<L>, List<R>>> toRightBiasedList() {
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
                return !lefts.isEmpty() ? Either.left(lefts) : Either.right(rights);
            } else {
                return !rights.isEmpty() ? Either.right(rights) : Either.left(lefts);
            }
        }
    }

}
