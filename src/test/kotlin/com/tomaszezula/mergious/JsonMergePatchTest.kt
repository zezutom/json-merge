package com.tomaszezula.mergious

import org.junit.jupiter.api.Test

/**
 * These tests verify compliance with Json Merge Patch specification defined by RfC 7396.
 * https://www.rfc-editor.org/rfc/rfc7396
 */
class JsonMergePatchTest {
    private val merger = DefaultMerger()

    @Test
    fun `simple patch`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent(),
            """
               {
                 "a":"c"
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"c"
               }
            """.trimIndent()
        )
    }

    @Test
    fun `union of non-overlapping fields`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent(),
            """
               {
                 "b":"c"
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"b",
                 "b":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null value removes an entire field`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":"b"
               }
            """.trimIndent(),
            """
               {
                 "a":null
               }
            """.trimIndent()
        )
        verifySuccess(mergeResult, "{}")
    }

    @Test
    fun `null value removes the relevant field only`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":"b",
                 "b":"c"
               }
            """.trimIndent(),
            """
               {
                 "a":null
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "b":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `patch overrides the field value even when data type doesn't match`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":["b"]
               }
            """.trimIndent(),
            """
               {
                 "a":"c"
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `patch overrides the field value even when data type doesn't match, either way`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":"c"
               }
            """.trimIndent(),
            """
               {
                 "a":["b"]
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":["b"]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null overlapping and non-overlapping fields combo works as expected`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":{
                    "b":"c"
                 }
               }
            """.trimIndent(),
            """
               {
                 "a":{
                   "b":"d",
                   "c":null
                 }
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
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
    fun `new array overrides the old one`() {
        val mergeResult = merger.merge(
            """
               {
                 "a":[
                    {"b":"c"}
                 ]
               }
            """.trimIndent(),
            """
               {
                 "a":[1]
               }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":[1]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `json array is replaced with a new one`() {
        val mergeResult = merger.merge(
            """
                ["a","b"]
            """.trimIndent(),
            """
                ["c","d"]
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
                ["c","d"]
            """.trimIndent()
        )
    }

    @Test
    fun `json object is replaced with a json array`() {
        val mergeResult = merger.merge(
            """
                {"a":"b"}
            """.trimIndent(),
            """
                ["c"]
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
                ["c"]
            """.trimIndent()
        )
    }

    @Test
    fun `json object is nullified`() {
        val mergeResult = merger.merge(
            """
                {"a":"foo"}
            """.trimIndent(), "null"
        )
        verifySuccess(
            mergeResult, "null"
        )
    }

    @Test
    fun `json object is replaced with json string`() {
        val mergeResult = merger.merge(
            """
                {"a":"foo"}
            """.trimIndent(), "bar"
        )
        verifySuccess(
            mergeResult, "bar"
        )
    }

    @Test
    fun `null field in the original object is preserved`() {
        val mergeResult = merger.merge(
            """
                {"e":null}
            """.trimIndent(),
            """
                {"a":1}
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
                {
                    "a":1,
                    "e":null
                 }
            """.trimIndent()
        )
    }

    @Test
    fun `null field in the other object is dropped`() {
        val mergeResult = merger.merge(
            """
                {}
            """.trimIndent(),
            """
                {
                    "a":"b",
                    "c":null
                }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
                {
                    "a":"b"
                 }
            """.trimIndent()
        )
    }

    @Test
    fun `json array is replaced with a json object`() {
        val mergeResult = merger.merge(
            """
                [1,2]
            """.trimIndent(),
            """
                {
                    "a":"b",
                    "c":null
                }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
                {"a":"b"}
            """.trimIndent()
        )
    }

    @Test
    fun `nested null field in the other object is dropped`() {
        val mergeResult = merger.merge(
            """
                {}
            """.trimIndent(),
            """
                {
                    "a": {
                      "bb": {
                        "ccc":null
                      }
                    }
                }
            """.trimIndent()
        )
        verifySuccess(
            mergeResult,
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