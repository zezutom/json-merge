package com.tomaszezula.mergious

import org.junit.jupiter.api.Test

class DefaultMergerTest {
    private val base = """
               {
                 "a": "b",
                 "c": {
                   "d": "e",
                   "f": "g"
                 }
               }
        """.trimIndent()

    private val merger = DefaultMerger()

    @Test
    fun `merge object`() {
        val other = """
               {
                 "a":"z",
                 "c": {
                   "f": null
                 }
               }
        """.trimIndent()
        val merged = merger.merge(base, other)
        when (merged) {
            is Success -> println(merged.json.prettyPrint())
            else -> println(merged)
        }
    }
}