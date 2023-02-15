package com.tomaszezula.jsonmerge.strategy

import com.tomaszezula.jsonmerge.toJson
import com.tomaszezula.jsonmerge.verifySuccess
import org.junit.jupiter.api.Test

class CombineMergeStrategyTest {

    private val strategy = CombineMergeStrategy()

    @Test
    fun `a primitive field value should be updated`() {
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
                 "a":["b", "c"]
               }
            """.trimIndent()
        )
    }

    @Test
    fun `two objects should be merged`() {
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
    fun `two arrays should be merged`() {
        val strategyResult = strategy.merge(
            """
               [{
                 "a":"b"
               }]
            """.trimIndent().toJson(),
            """
               [{
                 "b":"c"
               }, 1,2,3]
            """.trimIndent().toJson()
        )
        verifySuccess(
            strategyResult,
            """
               [{
                 "a":"b"
               },{
                 "b":"c"
               }, 1,2,3]
        """.trimIndent()
        )
    }

    @Test
    fun `a primitive value should be added to the original object`() {
        val strategyResult = strategy.merge(
            """
               {
                 "b":"c"
               }
            """.trimIndent().toJson(),
            "abc".toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "b":"c",
                 "merged":"abc"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `an array should be added to the original object`() {
        val strategyResult = strategy.merge(
            """
               {
                 "b":"c"
               }
            """.trimIndent().toJson(),
            """
               [{"d": "e"}, 1, "abc"]
            """.trimIndent().toJson()

        )
        verifySuccess(
            strategyResult,
            """
               {
                 "b":"c",
                 "merged":[{"d": "e"}, 1, "abc"]
               }
        """.trimIndent()
        )
    }

    @Test
    fun `object addition should prevent key collision`() {
        val strategyResult = strategy.merge(
            """
               {
                 "b":"c",
                 "merged":"result of the previous merge"
               }
            """.trimIndent().toJson(),
            "abc".toJson()
        )
        verifySuccess(
            strategyResult,
            """
               {
                 "b":"c",
                 "merged0":"abc",
                 "merged":"result of the previous merge"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `a primitive value should be appended to the original array`() {
        val strategyResult = strategy.merge(
            """
               [{
                 "b":"c"
               }]
            """.trimIndent().toJson(),
            "abc".toJson()
        )
        verifySuccess(
            strategyResult,
            """
               [{
                 "b":"c"
               }, "abc"]
        """.trimIndent()
        )
    }

    @Test
    fun `an array should be appended to the original array`() {
        val strategyResult = strategy.merge(
            """
               [{
                 "b":"c"
               }]
            """.trimIndent().toJson(),
            """
               [{"d": "e"}, 1, "abc"]
            """.trimIndent().toJson()

        )
        verifySuccess(
            strategyResult,
            """
               [{
                 "b":"c"                 
               }, {"d": "e"}, 1, "abc"]
        """.trimIndent()
        )
    }
}