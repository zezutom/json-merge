package com.tomaszezula.mergious.extensions

import com.tomaszezula.mergious.add
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class JSONObjectAddTest {

    @Test
    fun `should add a primitive value`() {
        val jsonObject = JSONObject()
        assertTrue(jsonObject.add("a", "abc").similar(JSONObject(mapOf("a" to "abc"))))
    }

    @Test
    fun `should add an object`() {
        val jsonObject = JSONObject()
        val addition = JSONObject(mapOf("a" to "abc"))
        assertTrue(jsonObject.add("a", addition).similar(JSONObject(mapOf("a" to addition))))
    }

    @Test
    fun `should add an array`() {
        val jsonObject = JSONObject()
        val addition = JSONArray(setOf(1, 2, 3))
        assertTrue(jsonObject.add("a", addition).similar(JSONObject(mapOf("a" to addition))))
    }

    @Test
    fun `should not modify the original object`() {
        val originalObject = JSONObject(mapOf("a" to "b", "b" to "c"))
        assertTrue(originalObject.add("c", "def").similar(JSONObject(mapOf("a" to "b", "b" to "c", "c" to "def"))))
        assertTrue(originalObject.similar(JSONObject(mapOf("a" to "b", "b" to "c"))))
    }
}