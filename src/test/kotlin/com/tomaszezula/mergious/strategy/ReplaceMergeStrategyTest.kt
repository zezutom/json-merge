package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.toJson
import com.tomaszezula.mergious.verifySuccess
import org.junit.jupiter.api.Test

class ReplaceMergeStrategyTest {

    private val strategy = ReplaceMergeStrategy()

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
                 "a":"c"
               }
            """.trimIndent()
        )
    }

    @Test
    fun `an object should be replaced`() {
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
                 "b":"c"
               }
        """.trimIndent()
        )
    }

    @Test
    fun `an array should be replaced`() {
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
                 "b":"c"
               }, 1,2,3]
        """.trimIndent()
        )
    }
}