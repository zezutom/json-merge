package com.tomaszezula.mergious.extensions

import com.tomaszezula.mergious.*
import io.kotest.matchers.shouldBe
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class StringToJsonTest {

    @Test
    fun `null should translate to JSON null`() {
        val nullString: String? = null
        nullString.toJson() shouldBe JsonNull
    }

    @Test
    fun `empty string should translate to JSON string`() {
        "".toJson() shouldBe JsonString("")
    }

    @Test
    fun `string literal should translate to JSON string`() {
        "abc".toJson() shouldBe JsonString("abc")
    }

    @Test
    fun `empty JSON object should translate to an empty JSON object`() {
        when (val json = "{}".toJson()) {
            is JsonObject -> assertTrue(json.value.similar(JSONObject()))
            else -> fail("Should be an object!")
        }
    }

    @Test
    fun `JSON object should translate to a JSON object`() {
        when (val json = "{'a':'b','c':{'d':'e'}}".toJson()) {
            is JsonObject -> {
                val jsonObject = JSONObject()
                val c = JSONObject()
                c.put("d", "e")
                jsonObject.put("a", "b")
                jsonObject.put("c", c)
                assertTrue(json.value.similar(jsonObject))
            }
            else -> fail("Should be an object!")
        }
    }

    @Test
    fun `JSON array should translate to JSON array`() {
        when (val json = "[1,2,3,4,5]".toJson()) {
            is JsonArray -> assertTrue(json.value.similar(JSONArray(setOf(1,2,3,4,5))))
            else -> fail("Should be an array!")
        }
    }
}