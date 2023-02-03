package com.tomaszezula.mergious

import org.junit.jupiter.api.Test

class MergeObjectInDefaultModeTest {
    private val merger = MergeBuilder().build()

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
    fun `existing member field should be updated with a new value and all other fields should be preserved`() {
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
                 "a":"z",
                 "c": {
                   "d":"e",
                   "f":"g"
                 }
               }
        """.trimIndent()
        )
    }

    @Test
    fun `null value should remove a field from the merged object`() {
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
            mergeResult, """
           {
             "a":"b"
           }
        """.trimIndent()
        )
    }

    @Test
    fun `null value should remove a nested field from the merged object`() {
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
             "c": {
               "f":null
             }
           }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
               {
                 "a":"b",
                 "c": {
                   "d":"e"
                 }
               }
            """.trimIndent()
        )
    }
}