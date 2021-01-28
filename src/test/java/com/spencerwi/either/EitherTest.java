package com.spencerwi.either;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Either with Java")
public class EitherTest {

    @Test
    public void canDecideWhichSideToBuild_givenSupplierMethods(){
        Either<String, Integer> leftSupplied = Either.either(()->"test", ()->null);
        assertThat(leftSupplied).isInstanceOf(Either.Left.class);
        assertThat(leftSupplied.getLeft()).isEqualTo("test");

        Either<String, Integer> rightSupplied = Either.either(()->null, ()->42);
        assertThat(rightSupplied).isInstanceOf(Either.Right.class);
        assertThat(rightSupplied.getRight()).isEqualTo(42);
    }

    @Test
    public void isRightBiased(){
        Either<String,Integer> bothSupplied = Either.either(()->"test", ()->42);

        assertThat(bothSupplied).isInstanceOf(Either.Right.class);
    }

    @Nested
    @DisplayName("Either.Left")
    public class EitherLeftTest {
        @Test
        public void canBeBuiltAsLeft(){
            Either<String, Integer> leftOnly = Either.left("test");

            assertThat(leftOnly).isInstanceOf(Either.Left.class);
        }

        @Test
        public void returnsLeftValueWhenAsked(){
            Either<String, Integer> leftOnly = Either.left("test");

            assertThat(leftOnly.getLeft()).isEqualTo("test");
        }
        @Test
        public void throwsWhenAskedForRight(){
            Either<String, Integer> leftOnly = Either.left("test");

            try {
                leftOnly.getRight();
            } catch(NoSuchElementException e){
                assertThat(e).hasMessageContaining("Tried to getRight from a Left");
            }
        }

        @Test
        public void identifiesAsLeftAndNotAsRight(){
            Either<String, Integer> leftOnly = Either.left("test");

            assertThat(leftOnly.isLeft()).isTrue();
            assertThat(leftOnly.isRight()).isFalse();
        }

        @Test
        public void executesLeftTransformation_whenFolded(){
            Either<String, Integer> leftOnly = Either.left("test");

            String result = leftOnly.fold(
                (leftSide)  -> leftSide.toUpperCase(),
                (rightSide) -> rightSide.toString()
            );

            assertThat(result).isEqualTo("TEST");
        }
        @Test
        public void executesLeftTransformation_whenMapped(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.map(
                (leftSide) -> leftSide.toUpperCase(),
                (rightSide) -> rightSide * 2
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("TEST");
        }
        @Test
        public void executesLeftTransformation_whenMappedLeft(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.mapLeft(
                (leftSide) -> leftSide.toUpperCase()
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("TEST");
        }
        @Test
        public void noOps_whenMappedRight(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.mapRight(
                (rightSide) -> rightSide * 2
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("test");
        }
		@Test
		public void executesLeftTransformationAndUnwraps_whenFlatMapped(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.flatMap(
                (leftSide) -> Either.left(leftSide.toUpperCase()),
                (rightSide) -> Either.right(rightSide * 2)
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("TEST");
		}
		@Test
		public void executesLeftTransformationAndUnwraps_whenFlatMappedLeft(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.flatMapLeft(
                (leftSide) -> Either.left(leftSide.toUpperCase())
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("TEST");
		}
		@Test
		public void noOps_whenFlatMappedRight(){
            Either<String, Integer> leftOnly = Either.left("test");

            Either<String, Integer> result = leftOnly.flatMapRight(
                (rightSide) -> Either.right(rightSide * 2)
            );

            assertThat(result).isInstanceOf(Either.Left.class);
            assertThat(result.getLeft()).isEqualTo("test");
		}
        @Test
        public void runsLeftConsumer_WhenRunIsCalled(){
            Either<String, Integer> leftOnly = Either.left("test");
            WrapperAroundBoolean leftHasBeenRun = new WrapperAroundBoolean(false);
            
            leftOnly.run( 
                left  -> { leftHasBeenRun.set(true); },
                right -> { /* no-op */ }
            );

            assertThat(leftHasBeenRun.get()).isEqualTo(true);
        }
        @Test
        void returnsValue_when_getLeftOrElseThrow() {
            Either<String, Integer> leftEither = Either.left("foo");

            assertThat(leftEither.getLeftOrElseThrow(() -> new RuntimeException("Int not accepted"))).isEqualTo("foo");
        }
        @Test
        void returnsValue_when_getLeftOrElseThrowWithRightFunction() {
            Either<String, Integer> leftEither = Either.left("foo");

            assertThat(leftEither.getLeftOrElseThrow(i -> new RuntimeException("Int not accepted " + i))).isEqualTo("foo");
        }
        @Test
        void throwsSuppliedException_when_getRightOrElseThrow() {
            Either<String, Integer> leftEither = Either.left("foo");

            assertThatThrownBy(() -> leftEither.getRightOrElseThrow(() -> new RuntimeException("String not accepted")))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("String not accepted");
        }
        @Test
        void throwsTransformedLeftException_when_getRightOrElseThrow() {
            Either<String, Integer> leftEither = Either.left("foo");

            assertThatThrownBy(() -> leftEither.getRightOrElseThrow(s -> new RuntimeException("String not accepted " + s)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("String not accepted foo");
        }
        @Test
        public void isEqualToOtherLeftsHavingTheSameLeftValue(){
            Either<String,Integer> leftIsTest = Either.left("test");
            Either<String,Object> leftIsAlsoTest = Either.left("test");

            assertThat(leftIsTest).isEqualTo(leftIsAlsoTest);
        }
        @Test
        public void isNotEqualToOtherLeftsHavingDifferentValues(){
            Either<String,Integer> leftIsHello = Either.left("hello");
            Either<String,Integer> leftIsWorld = Either.left("world");

            assertThat(leftIsHello).isNotEqualTo(leftIsWorld);
        }
        @Test
        public void isNotEqualToObjectsOfOtherClasses_obviously(){
            String hello = "hello";
            Either<String,Integer> leftIsHello = Either.left(hello);

            assertThat(leftIsHello).isNotEqualTo(hello);
        }
        @Test
        public void hasSameHashCodeAsWrappedLeftValue(){
            Either<String, Integer> leftOnly = Either.left("Test");

            assertThat(leftOnly.hashCode()).isEqualTo(leftOnly.getLeft().hashCode());
        }
    }

    @Nested
    @DisplayName("Either.Right")
    public class EitherRightTest {
        @Test
        public void canBeBuiltAsRight(){
            Either<String, Integer> rightOnly = Either.right(42);

            assertThat(rightOnly).isInstanceOf(Either.Right.class);
        }

        @Test
        public void returnsRightValueWhenAsked(){
            Either<String, Integer> rightOnly = Either.right(42);

            assertThat(rightOnly.getRight()).isEqualTo(42);
        }
        @Test
        public void throwsWhenAskedForLeft(){
            Either<String, Integer> rightOnly = Either.right(42);

            try {
                rightOnly.getLeft();
            }catch(NoSuchElementException e){
                assertThat(e).hasMessageContaining("Tried to getLeft from a Right");
            }
        }

        @Test
        public void identifiesAsRightAndNotAsLeft(){
            Either<String, Integer> rightOnly = Either.right(42);

            assertThat(rightOnly.isRight()).isTrue();
            assertThat(rightOnly.isLeft()).isFalse();
        }

        @Test
        public void executesRightTransformation_whenFolded(){
            Either<String, Integer> rightOnly = Either.right(42);

            String result = rightOnly.fold(
                (leftSide)  -> leftSide.toUpperCase(),
                (rightSide) -> rightSide.toString()
            );

            assertThat(result).isEqualTo("42");
        }
        @Test
        public void executesRightTransformation_whenMapped(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.map(
                (leftSide) -> leftSide.toUpperCase(),
                (rightSide) -> rightSide * 2
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42*2);
        }
        @Test
        public void executesRightTransformation_whenMappedRight(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.mapRight(
                (rightSide) -> rightSide * 2
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42*2);
        }
        @Test
        public void noOps_whenMappedLeft(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.mapLeft(
                (leftSide) -> leftSide.toUpperCase()
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42);
        }
        @Test
        public void executesRightTransformationAndUnwraps_whenFlatMapped(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.flatMap(
                (leftSide) -> Either.left(leftSide.toUpperCase()),
                (rightSide) -> Either.right(rightSide * 2)
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42*2);
        }
        @Test
        public void executesRightTransformationAndUnwraps_whenFlatMappedRight(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.flatMapRight(
                (rightSide) -> Either.right(rightSide * 2)
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42*2);
        }
        @Test
        public void noOps_whenFlatMappedLeft(){
            Either<String, Integer> rightOnly = Either.right(42);

            Either<String, Integer> result = rightOnly.flatMapLeft(
                (leftSide) -> Either.left(leftSide.toUpperCase())
            );

            assertThat(result).isInstanceOf(Either.Right.class);
            assertThat(result.getRight()).isEqualTo(42);
        }
        @Test
        public void runsRightConsumer_WhenRunIsCalled(){
            Either<String, Integer> rightOnly = Either.right(42);
            WrapperAroundBoolean rightHasBeenRun = new WrapperAroundBoolean(false);
            
            rightOnly.run( 
                left  -> { /* no-op */ },
                right -> { rightHasBeenRun.set(true);  }
            );

            assertThat(rightHasBeenRun.get()).isEqualTo(true);
        }
        @Test
        void returnsValue_when_getRightOrElseThrow() {
            Either<String, Integer> rightEither = Either.right(1);
            assertThat(rightEither.getRightOrElseThrow(() -> new RuntimeException("Int not accepted"))).isEqualTo(1);
        }
        @Test
        void returnsValue_when_getRightOrElseThrowWithLeftFunction() {
            Either<String, Integer> rightEither = Either.right(1);
            assertThat(rightEither.getRightOrElseThrow(s -> new RuntimeException("Int not accepted " + s))).isEqualTo(1);
        }
        @Test
        void throwsSuppliedException_when_getLeftOrElseThrow() {
            Either<String, Integer> rightEither = Either.right(1);

            assertThatThrownBy(() -> rightEither.getLeftOrElseThrow(() -> new RuntimeException("String not accepted")))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("String not accepted");
        }
        @Test
        void throwsTransformedRightException_when_getLeftOrElseThrow() {
            Either<String, Integer> rightEither = Either.right(1);

            assertThatThrownBy(() -> rightEither.getLeftOrElseThrow(s -> new RuntimeException("String not accepted " + s)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("String not accepted 1");
        }
        @Test
        public void isEqualToOtherRightsHavingTheSameRightValue(){
            Either<String,Integer> rightIs42 = Either.right(42);
            Either<Object,Integer> rightIsAlso42 = Either.right(42);

            assertThat(rightIs42).isEqualTo(rightIsAlso42);
        }
        @Test
        public void isNotEqualToOtherRightsHavingDifferentValues(){
            Either<String,Integer> rightIs42 = Either.right(42);
            Either<String,Integer> rightIs9001 = Either.right(9001);

            assertThat(rightIs42).isNotEqualTo(rightIs9001);
        }
        @Test
        public void isNotEqualToObjectsOfOtherClasses_obviously(){
            Integer fortyTwo = 42;
            Either<String,Integer> rightIs42 = Either.right(fortyTwo);

            assertThat(rightIs42).isNotEqualTo(fortyTwo);
        }
        @Test
        public void hasSameHashCodeAsWrappedRightValue(){
            Either<String, Integer> rightOnly = Either.right(42);

            assertThat(rightOnly.hashCode()).isEqualTo(rightOnly.getRight().hashCode());
        }
    }

    public static class WrapperAroundBoolean {
        private boolean value;
        public WrapperAroundBoolean(){ this.value = false; }
        public WrapperAroundBoolean(boolean value) { this.value = value; }
        public void set(boolean value){ this.value = value; }
        public boolean get(){ return this.value; }
    }
}
