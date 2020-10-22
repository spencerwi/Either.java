package com.spencerwi.either;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EitherCollectors with Java")
public class EitherCollectorsTest {

    @Test
    public void checkBiasedCollectors(){
        Either<String, Integer> either1 = Either.left("left");
        Either<String, Integer> either2 = Either.right(42);

        List<Either<String, Integer>> listOfEithers = Stream.of(either1, either2).collect(Collectors.toList());

        // left
        Either<List<String>, List<Integer>> leftBiasedList = listOfEithers.stream().collect(EitherCollectors.toLeftBiasedList());

        assertThat(leftBiasedList).isInstanceOf(Either.Left.class);
        assertThat(leftBiasedList.getLeft().size()).isEqualTo(1);
        assertThat(leftBiasedList.getLeft().get(0)).isEqualTo(either1.getLeft());

        // right
        Either<List<String>, List<Integer>> rightBiasedList = listOfEithers.stream().collect(EitherCollectors.toRightBiasedList());

        assertThat(rightBiasedList).isInstanceOf(Either.Right.class);
        assertThat(rightBiasedList.getRight().size()).isEqualTo(1);
        assertThat(rightBiasedList.getRight().get(0)).isEqualTo(either2.getRight());
    }


    @Test
    public void checkEmptyStreamLogic(){
        Either<List<String>, List<Integer>> leftBiasedList =
                new ArrayList< Either<String, Integer> >().stream().collect(EitherCollectors.toLeftBiasedList());

        assertThat(leftBiasedList).isInstanceOf(Either.Left.class);
        assertThat(leftBiasedList.getLeft()).isEmpty();

        Either<List<String>, List<Integer>> rightBiasedList =
                new ArrayList< Either<String, Integer> >().stream().collect(EitherCollectors.toRightBiasedList());

        assertThat(rightBiasedList).isInstanceOf(Either.Right.class);
        assertThat(rightBiasedList.getRight()).isEmpty();
    }

}
