package com.tomaszezula.jsonmerge

import io.kotest.matchers.shouldBe
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import kotlin.test.fail

class MergeResultTest {

    @Test
    fun `success should get a string representation of the underlying JSON object`() {
        val underlying = JSONObject(
            """
            {
              "a": "b",
              "c": [1, 2, 3]
            }
        """.trimIndent()
        )
        val success = Success(JsonObject(underlying))
        success.getOrThrow() shouldBe underlying.toString()
    }

    @Test
    fun `success should get a pretty-print string representation of the underlying JSON object`() {
        val underlying = JSONObject(
            """
            {
              "a": "b",
              "c": [1, 2, 3]
            }
        """.trimIndent()
        )
        val success = Success(JsonObject(underlying))
        success.getOrThrow(prettyPrint = true) shouldBe success.json.prettyPrint()
    }

    @Test
    fun `success should get a string representation of the underlying JSON array`() {
        val underlying = JSONArray(listOf(1, 2, 3))
        val success = Success(JsonArray(underlying))
        success.getOrThrow() shouldBe underlying.toString()
    }

    @Test
    fun `success should get a string representation of the underlying JSON string`() {
        val underlying = "abc"
        val success = Success(JsonString(underlying))
        success.getOrThrow() shouldBe underlying
    }

    @Test
    fun `success should get a string representation of the underlying JSON null`() {
        val success = Success(JsonNull)
        success.getOrThrow() shouldBe "null"
    }

    @Test
    fun `failure should throw the underlying exception`() {
        val underlying = IllegalStateException("Illegal stuff")
        val failure = Failure("", underlying)
        try {
            failure.getOrThrow()
            fail("Should not pass!")
        } catch (e: IllegalStateException) {
            e shouldBe underlying
        }
    }

    @Test
    fun `failure should throw an illegal state exception using the custom message`() {
        val underlying = "Illegal stuff"
        val failure = Failure(underlying, null)
        try {
            failure.getOrThrow()
            fail("Should not pass!")
        } catch (e: IllegalStateException) {
            e shouldBe IllegalStateException(underlying)
        }
    }
}