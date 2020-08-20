package com.spencerwi.either

import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Result with Kotlin")
class ResultKtTest {
    @Nested
    @DisplayName("Result.Err with Kotlin")
    inner class ResultErrKtTest {
        @Test
        fun `can be used with type-based pattern-matching`() {
            val err : Result<Int> = Result.err(RuntimeException("Test"))
            when (err) {
                is Result.Err<Int> -> assertThat(err.exception.message.equals("Test"))
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `is not useful for equality-based pattern-matching because exceptions don't have useful equality definitions`() {
            val err : Result<Int> = Result.err(RuntimeException("Test"))
            when (err) {
                Result.err<Int>(RuntimeException("Test")) -> fail("This won't work, unfortunately, because two RuntimeExceptions with the same message aren't equal.")
                else -> true
            }
        }
    }

    @Nested
    @DisplayName("Result.Ok with Kotlin")
    inner class ResultOkKtTest {
        @Test
        fun `can be used with type-based pattern-matching`() {
            val ok = Result.ok(42)
            when (ok) {
                is Result.Ok<Int> -> assertThat(ok.result == 42)
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be used with equality-based pattern-matching`() {
            val ok = Result.ok(42)
            when (ok) {
                Result.ok(42) -> true
                else -> fail("Pattern-matching failed!")
            }
        }
        @Test
        fun `can be mapped using Kotlin lambdas`() {
            val ok = Result.ok(42)
            val result = ok.map {it * 2}
            assertThat(result.result).isEqualTo(84)
        }
    }
}