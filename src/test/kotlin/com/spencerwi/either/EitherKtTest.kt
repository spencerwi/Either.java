package com.spencerwi.either

import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Either with Kotlin")
class EitherKtTest {
    @Nested
    @DisplayName("Either.Left with Kotlin")
    inner class EitherLeftKtTest {
        @Test
        fun `can be used with type-based pattern-matching`() {
            val left = Either.left<Int, String>(42);
            when (left) {
                is Either.Left<Int, String> -> assertThat(left.leftValue == 42)
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be used with equality-based pattern-matching`() {
            val left = Either.left<Int, String>(42);
            when (left) {
                Either.left<Int, String>(42) -> true
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be left-mapped using Kotlin lambdas`() {
            val left = Either.left<Int, String>(42)
            val result = left.mapLeft {it * 2}
            assertThat(result.left).isEqualTo(42 * 2)
        }
        @Test
        fun `can be left-flat-mapped using Kotlin lambdas`() {
            val left = Either.left<Int, String>(42)
            val result = left.flatMapLeft {
                Either.left(it * 2)
            }
            assertThat(result.left).isEqualTo(42 * 2)
        }
    }

    @Nested
    @DisplayName("Either.Right with Kotlin")
    inner class EitherRightKtTest {
        @Test
        fun `can be used with type-based pattern-matching`() {
            val right = Either.right<Int, String>("Yay");
            when (right) {
                is Either.Right<Int, String> -> assertThat(right.rightValue == "Yay")
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be used with equality-based pattern-matching`() {
            val right = Either.right<Int, String>("Yay");
            when (right) {
                Either.right<Int, String>("Yay") -> true
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be right-mapped using Kotlin lambdas`() {
            val right = Either.right<Int, String>("Yay");
            val result = right.mapRight { it.toUpperCase() }
            assertThat(result.right).isEqualTo("YAY")
        }
        @Test
        fun `can be right-flat-mapped using Kotlin lambdas`() {
            val right = Either.right<Int, String>("Yay");
            val result = right.flatMapRight {
                Either.right(it.toUpperCase())
            }
            assertThat(result.right).isEqualTo("YAY")
        }
    }
}