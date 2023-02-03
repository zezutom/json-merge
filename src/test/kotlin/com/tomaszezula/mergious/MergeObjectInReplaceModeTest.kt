package com.tomaszezula.mergious

import org.junit.jupiter.api.Test

class MergeObjectInReplaceModeTest {

    private val merger = MergeBuilder().withReplaceMode().build()

    @Test
    fun `a primitive field should be updated with a new value`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b"
           }
        """.trimIndent(),
            """
           {
             "a":"z"
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"z"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `existing member field should be updated with a new value and all fields are dropped`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b",
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent(),
            """
           {
             "a":"z"
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"z"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null value should be dropped`() {
        val mergeResult = merger.merge(
            """
           {
             "a":"b",
             "c": {
               "d":"e",
               "f":"g"
             }
           }
        """.trimIndent(),
            """
           {
             "c":null
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult, "{}"
        )
    }
}