package com.tomaszezula.mergious.extensions

import com.tomaszezula.mergious.append
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class JSONArrayAppendTest {

    @Test
    fun `should append a primitive value`() {
        val jsonArray = JSONArray(setOf(1, 2, 3))
        assertTrue(jsonArray.append("four").similar(JSONArray(setOf(1, 2, 3, "four"))))
    }

    @Test
    fun `should append an object`() {
        val jsonArray = JSONArray(setOf(1, 2, 3))
        val addition = JSONObject(mapOf("a" to "b"))
        assertTrue(jsonArray.append(addition).similar(JSONArray(setOf(1, 2, 3, addition))))
    }

    @Test
    fun `should append an array`() {
        val jsonArray = JSONArray(setOf(1, 2, 3))
        val addition = JSONArray(setOf(4, 5, 6))
        assertTrue(jsonArray.append(addition).similar(JSONArray(listOf(1, 2, 3, 4, 5, 6))))
    }

    @Test
    fun `append should preserve duplicates`() {
        val jsonArray = JSONArray(setOf(1, 2, 3))
        val addition = JSONArray(setOf(1, 2, 3))
        assertTrue(jsonArray.append(addition).similar(JSONArray(listOf(1, 2, 3, 1, 2, 3))))
    }

    @Test
    fun `should not modify the original array`() {
        val originalArray = JSONArray(setOf(1, 2, 3))
        val addition = JSONArray(setOf(4))
        assertTrue(originalArray.append(addition).similar(JSONArray(listOf(1, 2, 3, 4))))
        assertTrue(originalArray.similar(JSONArray(setOf(1, 2, 3))))
    }
}