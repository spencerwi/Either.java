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
        fun `can be used with pattern-matching`() {
            val err : Result<Int> = Result.err(RuntimeException("Test"))
            when (err) {
                is Result.Err<Int> -> assertThat(err.exception.message.equals("Test"))
                else -> fail("Pattern-matching failed!")
            }
        }
    }

    @Nested
    @DisplayName("Result.Ok with Kotlin")
    inner class ResultOkKtTest {
        @Test
        fun `can be used with pattern-matching`() {
            val ok = Result.ok(42)
            when (ok) {
                is Result.Ok<Int> -> assertThat(ok.result == 42)
                else -> fail("Pattern-matching failed!")
            }
        }
    }
}