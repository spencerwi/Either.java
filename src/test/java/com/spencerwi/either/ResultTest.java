
package com.spencerwi.either;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


public class ResultTest {

    @Test
    public void attempt_WhenGivenSupplierIsSuccessful_WrapsResult(){
        Result<Integer> successful = Result.attempt(() -> 1 + 1);
        assertThat(successful).isInstanceOf(Result.Ok.class);
        assertThat(successful.getResult()).isEqualTo(2);
    }

    @Test
    public void attempt_WhenSupplierThrows_WrapsException(){
        Exception ex = new Exception("Error! Failed!");
        Result<Integer> failed = Result.attempt(()->{
            throw ex;
        });

        assertThat(failed).isInstanceOf(Result.Err.class);
        assertThat(failed.getException()).isEqualTo(ex);
    }

    @Nested
    @DisplayName("Result.Err")
    public class ResultErrTest {
        @Test
        public void canBeBuiltAsErr(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            assertThat(errOnly).isInstanceOf(Result.Err.class);
        }

        @Test
        public void returnsExceptionWhenAsked(){
            Exception ex = new Exception("Error! Failed!");
            Result<Integer> errOnly = Result.err(ex);

            assertThat(errOnly.getException()).isEqualTo(ex);
        }
        @Test
        public void throwsWhenAskedForResult(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            try {
                errOnly.getResult();
            } catch(NoSuchElementException e){
                assertThat(e).hasMessageContaining("Tried to getResult from an Err");
            }
        }

        @Test
        public void identifiesAsErrAndNotAsOk(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            assertThat(errOnly.isErr()).isTrue();
            assertThat(errOnly.isOk()).isFalse();
        }

        @Test
        public void executesErrTransformation_whenFolded(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            String result = errOnly.fold(
                errSide   -> errSide.getMessage(),
                valueSide -> valueSide.toString()
            );

            assertThat(result).isEqualTo("Error! Failed!");
        }
        @Test
        public void doesNotModifyException_whenMapped(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            Result<Integer> result = errOnly.map((valueSide) -> valueSide * 2);

            assertThat(result).isInstanceOf(Result.Err.class);
            assertThat(result.getException()).hasMessageContaining("Error! Failed!");
        }
        @Test
        public void doesNotModifyException_whenFlatMapped(){
            Result<Integer> errOnly = Result.err(new Exception("Error! Failed!"));

            Result<Integer> result = errOnly.flatMap((valueSide) -> Result.ok(valueSide * 2));

            assertThat(result).isInstanceOf(Result.Err.class);
            assertThat(result.getException()).hasMessageContaining("Error! Failed!");
        }
        @Test
        public void isEqualToOtherErrsHavingTheSameErrValue(){
            Exception ex = new Exception("Test");
            Result<Integer> errIsTest = Result.err(ex);
            Result<Object> errIsAlsoTest = Result.err(ex);

            assertThat(errIsTest).isEqualTo(errIsAlsoTest);
        }
        @Test
        public void isNotEqualToOtherErrsHavingDifferentValues(){
            Result<Integer> errIsHello = Result.err(new Exception("hello"));
            Result<Integer> errIsWorld = Result.err(new Exception("world"));

            assertThat(errIsHello).isNotEqualTo(errIsWorld);
        }
        @Test
        public void isNotEqualToObjectsOfOtherClasses_obviously(){
            String hello = "hello";
            Result<Integer> errIsHello = Result.err(new Exception(hello));

            assertThat(errIsHello).isNotEqualTo(hello);
        }
        @Test
        public void hasSameHashCodeAsWrappedException(){
            Result<Integer> errOnly = Result.err(new Exception("Test"));

            assertThat(errOnly.hashCode()).isEqualTo(errOnly.getException().hashCode());
        }
    }

    @Nested
    @DisplayName("Result.Ok")
    public class ResultOkTest {
        @Test
        public void canBeBuiltAsOk(){
            Result<Integer> ok = Result.ok(42);

            assertThat(ok).isInstanceOf(Result.Ok.class);
        }

        @Test
        public void returnsResultWhenAsked(){
            Result<Integer> ok = Result.ok(42);

            assertThat(ok.getResult()).isEqualTo(42);
        }
        @Test
        public void throwsWhenAskedForException(){
            Result<Integer> ok = Result.ok(42);

            try {
                ok.getException();
            }catch(NoSuchElementException e){
                assertThat(e).hasMessageContaining("Tried to getException from an Ok");
            }
        }

        @Test
        public void identifiesAsOkAndNotAsErr(){
            Result<Integer> ok = Result.ok(42);

            assertThat(ok.isOk()).isTrue();
            assertThat(ok.isErr()).isFalse();
        }

        @Test
        public void executesResultTransformation_whenFolded(){
            Result<Integer> ok = Result.ok(42);

            String okStr = ok.fold(
                err     -> err.getMessage(),
                result  -> result.toString()
            );

            assertThat(okStr).isEqualTo("42");
        }
        @Test
        public void transformsToResult_whenMapped(){
            Result<Integer> ok = Result.ok(42);

            Result<Integer> okTimes2 = ok.map(result -> result * 2);

            assertThat(okTimes2).isInstanceOf(Result.Ok.class);
            assertThat(okTimes2.getResult()).isEqualTo(42*2);
        }
        @Test
        public void correctlyFlattens_whenFlatMapped(){
            Result<Integer> ok = Result.ok(42);

            Result<Integer> okTimes2 = ok.flatMap(result -> Result.ok(result * 2));

            assertThat(okTimes2).isInstanceOf(Result.Ok.class);
            assertThat(okTimes2.getResult()).isEqualTo(42*2);
        }
        @Test
        public void isEqualToOtherOksHavingTheSameResultValue(){
            Result<Integer> resultIs42     = Result.ok(42);
            Result<Integer> resultIsAlso42 = Result.ok(42);

            assertThat(resultIs42).isEqualTo(resultIsAlso42);
        }
        @Test
        public void isNotEqualToOtherOksHavingDifferentValues(){
            Result<Integer> resultIs42   = Result.ok(42);
            Result<Integer> resultIs9001 = Result.ok(9001);

            assertThat(resultIs42).isNotEqualTo(resultIs9001);
        }
        @Test
        public void isNotEqualToObjectsOfOtherClasses_obviously(){
            Integer fortyTwo = 42;
            Result<Integer> resultIs42 = Result.ok(fortyTwo);

            assertThat(resultIs42).isNotEqualTo(fortyTwo);
        }
        @Test
        public void hasSameHashCodeAsWrappedRightValue(){
            Result<Integer> resultOnly = Result.ok(42);

            assertThat(resultOnly.hashCode()).isEqualTo(resultOnly.getResult().hashCode());
        }
        @Test
        public void mapToErr() {
            Result<?> result = Result.attempt(() -> null)
                    .map(Objects::requireNonNull);
            assertThat(result.isErr()).isTrue();
            assertThat(result.getException()).isInstanceOf(NullPointerException.class);
        }
    }
}
