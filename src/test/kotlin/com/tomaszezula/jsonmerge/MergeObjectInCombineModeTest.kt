package com.tomaszezula.jsonmerge

import org.junit.jupiter.api.Test

class MergeObjectInCombineModeTest {

    private val merger = MergeBuilder().withCombineMode().build()

    @Test
    fun `primitive fields should be merged into an array`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b"
           }
        """.trimIndent(), """
           {
             "a":"z"
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult, """
               {
                 "a":["b", "z"]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `all values should be preserved in a complex object merge`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b",
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent(), """
           {
             "a":"z"
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult, """
           {
             "a":["b","z"],
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent()
        )
    }

    @Test
    fun `null value in the other JSON should be ignored`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b",
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent(), """
           {
             "c":null
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult, """
           {
             "a":"b",
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent()
        )
    }
}