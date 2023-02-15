package com.tomaszezula.jsonmerge.extensions

import com.tomaszezula.jsonmerge.removeNulls
import org.json.JSONObject
import org.json.JSONObject.NULL
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class JSONObjectRemoveNullsTest {

    @Test
    fun `should remove all null fields from a JSON object`() {
        val jsonObject = JSONObject(mapOf("a" to "b", "c" to NULL, "e" to NULL, "g" to 1))
        assertTrue(jsonObject.removeNulls().similar(JSONObject(mapOf("a" to "b", "g" to 1))))
    }

    @Test
    fun `should work recursively`() {
        val jsonObject = JSONObject(
            mapOf(
                "a" to "b",
                "c" to JSONObject(
                    mapOf(
                        "d" to "e",
                        "f" to NULL,
                        "g" to JSONObject(
                            mapOf(
                                "a" to NULL,
                                "b" to 10,
                                "c" to NULL,
                                "d" to true
                            )
                        )
                    )
                ),
                "d" to 123
            )
        )
        assertTrue(jsonObject.removeNulls().similar(
            JSONObject(
                mapOf(
                    "a" to "b",
                    "c" to JSONObject(
                        mapOf(
                            "d" to "e",
                            "g" to JSONObject(
                                mapOf(
                                    "b" to 10,
                                    "d" to true
                                )
                            )
                        )
                    ),
                    "d" to 123
                )
            )
        ))
    }

    @Test
    fun `should not modify the original object`() {
        val originalObject = JSONObject(mapOf("a" to "b", "c" to NULL))
        assertTrue(originalObject.removeNulls().similar(JSONObject(mapOf("a" to "b"))))
        assertTrue(originalObject.similar(JSONObject(mapOf("a" to "b", "c" to NULL))))
    }
}