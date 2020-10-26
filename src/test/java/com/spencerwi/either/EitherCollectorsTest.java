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
        Either<String, Integer> eitherLeft1 = Either.left("left1");
        Either<String, Integer> eitherLeft2 = Either.left("left2");

        Either<String, Integer> eitherRight1 = Either.right(1);
        Either<String, Integer> eitherRight2 = Either.right(2);
        Either<String, Integer> eitherRight3 = Either.right(3);

        List<Either<String, Integer>> listOfEithers = Stream.of(eitherLeft1, eitherRight1, eitherLeft2, eitherRight2, eitherRight3)
                .collect(Collectors.toList());

        // left
        Either<List<String>, List<Integer>> eitherLeftList = listOfEithers.stream().collect(EitherCollectors.toLeftBiased());

        assertThat(eitherLeftList).isInstanceOf(Either.Left.class);
        assertThat(eitherLeftList.getLeft().size()).isEqualTo(2);

        // right
        Either<List<String>, List<Integer>> eitherRightList = listOfEithers.stream().collect(EitherCollectors.toRightBiased());

        assertThat(eitherRightList).isInstanceOf(Either.Right.class);
        assertThat(eitherRightList.getRight().size()).isEqualTo(3);
    }


    @Test
    public void checkEmptyStreamLogic(){
        Either<List<String>, List<Integer>> eitherLeftList =
                new ArrayList< Either<String, Integer> >().stream().collect(EitherCollectors.toLeftBiased());

        assertThat(eitherLeftList).isInstanceOf(Either.Left.class);
        assertThat(eitherLeftList.getLeft()).isEmpty();

        Either<List<String>, List<Integer>> eitherRightList =
                new ArrayList< Either<String, Integer> >().stream().collect(EitherCollectors.toRightBiased());

        assertThat(eitherRightList).isInstanceOf(Either.Right.class);
        assertThat(eitherRightList.getRight()).isEmpty();
    }

}
