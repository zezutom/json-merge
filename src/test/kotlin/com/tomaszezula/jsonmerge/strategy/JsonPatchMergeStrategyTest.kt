package com.tomaszezula.jsonmerge.strategy

import com.tomaszezula.jsonmerge.toJson
import com.tomaszezula.jsonmerge.verifySuccess
import org.junit.jupiter.api.Test

/**
 * These tests verify compliance with Json Merge Patch specification defined by RfC 7396.
 * https://www.rfc-editor.org/rfc/rfc7396
 */
class JsonPatchMergeStrategyTest {

    private val strategy = JsonPatchMergeStrategy()

    @Test
    fun `simple patch`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":"c"
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":"c"
               }
            """.trimIndent()
        )
    }

    @Test
    fun `union of non-overlapping fields`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent().toJson(),
            """
               {
                 "b":"c"
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":"b",
                 "b":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null value should remove an entire field`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":null
               }
            """.trimIndent().toJson()
        )
        verifySuccess(strategyResult, "{}")
    }

    @Test
    fun `null value should remove the relevant field only`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":"b",
                 "b":"c"
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":null
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "b":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `patch should override the field value even when data type doesn't match`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":["b"]
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":"c"
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `patch should override the field value even when data type doesn't match, either way`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":"c"
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":["b"]
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":["b"]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null overlapping and non-overlapping fields combo should work as expected`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":{
                    "b":"c"
                 }
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":{
                   "b":"d",
                   "c":null
                 }
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":{
                    "b":"d"
                 }
               }
        """.trimIndent()
        )
    }

    @Test
    fun `new array should override the old one`() {
        val strategyResult = strategy.merge(
            """
               {
                 "a":[
                    {"b":"c"}
                 ]
               }
            """.trimIndent().toJson(),
            """
               {
                 "a":[1]
               }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "a":[1]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `json array should be replaced with a new one`() {
        val strategyResult = strategy.merge(
            """
                ["a","b"]
            """.trimIndent().toJson(),
            """
                ["c","d"]
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                ["c","d"]
            """.trimIndent()
        )
    }

    @Test
    fun `json object should be replaced with a json array`() {
        val strategyResult = strategy.merge(
            """
                {"a":"b"}
            """.trimIndent().toJson(),
            """
                ["c"]
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                ["c"]
            """.trimIndent()
        )
    }

    @Test
    fun `json object should be nullified`() {
        val strategyResult = strategy.merge(
            """
                {"a":"foo"}
            """.trimIndent().toJson(), "null".toJson()
        )
        verifySuccess(
            strategyResult, "null"
        )
    }

    @Test
    fun `json object should be replaced with json string`() {
        val strategyResult = strategy.merge(
            """
                {"a":"foo"}
            """.trimIndent().toJson(), "bar".toJson()
        )
        verifySuccess(
            strategyResult, "bar"
        )
    }

    @Test
    fun `null field in the original object should be preserved`() {
        val strategyResult = strategy.merge(
            """
                {"e":null}
            """.trimIndent().toJson(),
            """
                {"a":1}
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                {
                    "a":1,
                    "e":null
                 }
            """.trimIndent()
        )
    }

    @Test
    fun `null field in the other object should be dropped`() {
        val strategyResult = strategy.merge(
            """
                {}
            """.trimIndent().toJson(),
            """
                {
                    "a":"b",
                    "c":null
                }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                {
                    "a":"b"
                 }
            """.trimIndent()
        )
    }

    @Test
    fun `json array should be replaced with a json object`() {
        val strategyResult = strategy.merge(
            """
                [1,2]
            """.trimIndent().toJson(),
            """
                {
                    "a":"b",
                    "c":null
                }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                {"a":"b"}
            """.trimIndent()
        )
    }

    @Test
    fun `nested null field in the other object should be dropped`() {
        val strategyResult = strategy.merge(
            """
                {}
            """.trimIndent().toJson(),
            """
                {
                    "a": {
                      "bb": {
                        "ccc":null
                      }
                    }
                }
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
                {
                    "a": {
                      "bb": {}
                    }
                 }
            """.trimIndent()
        )
    }
}