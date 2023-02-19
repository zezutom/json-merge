package com.tomaszezula.jsonmerge

import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test

class MergeWithJsonPathTest {

    private val merger = MergeBuilder().build()

    @Test
    fun `the operation should fail if the json path expression is invalid`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("invalid path", original, "new value")
        verifyFailure(mergeResult)
    }

    @Test
    fun `the operation should fail if the json path expression is empty`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("", original, "new value")
        verifyFailure(mergeResult)
    }

    @Test
    fun `the original object should be returned if json path does not exist`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.a", original, "new value")
        verifySuccess(
            mergeResult,
            original
        )
    }

    @Test
    fun `the respective property should be replaced with the provided value`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.f", original, false)
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": false,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `the respective property should be replaced with the provided value even when the data type does not match`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.f", original, 123)
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": 123,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json array literal`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.f", original, "[1,2,3]")
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": [1, 2, 3],
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json array`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.f", original, JSONArray(listOf(1, 2, 3)))
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": [1, 2, 3],
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json array using a wrapper`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge("\$.c.f", original, JsonArray(JSONArray(listOf(1, 2, 3))))
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": [1, 2, 3],
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json object literal`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge(
            "\$.c.f", original, """
            {
              "message": "hi",
              "x": 10,
              "y": [1, 2, 3],
              "z": {
                "a": "b",
                "c": {
                  "d": true
                }
              }
            }
        """.trimIndent()
        )
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": {
                    "message": "hi",
                    "x": 10,
                    "y": [1, 2, 3],
                    "z": {
                      "a": "b",
                      "c": {
                        "d": true
                      }
                    }
                  },
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json object`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge(
            "\$.c.f", original, JSONObject(
                """
                    {
                      "message": "hi",
                      "x": 10,
                      "y": [1, 2, 3],
                      "z": {
                        "a": "b",
                        "c": {
                          "d": true
                        }
                      }
                    }
                """.trimIndent()
            )
        )
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": {
                    "message": "hi",
                    "x": 10,
                    "y": [1, 2, 3],
                    "z": {
                      "a": "b",
                      "c": {
                        "d": true
                      }
                    }
                  },
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }

    @Test
    fun `it should be possible to replace a property with a json object using a wrapper`() {
        val original = """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": true,
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()

        val mergeResult = merger.merge(
            "\$.c.f", original, JsonObject(
                JSONObject(
                    """
                    {
                      "message": "hi",
                      "x": 10,
                      "y": [1, 2, 3],
                      "z": {
                        "a": "b",
                        "c": {
                          "d": true
                        }
                      }
                    }
                """.trimIndent()
                )
            )
        )
        verifySuccess(
            mergeResult,
            """
            {
                "a": "b",
                "c": {
                  "d": "e",
                  "f": {
                    "message": "hi",
                    "x": 10,
                    "y": [1, 2, 3],
                    "z": {
                      "a": "b",
                      "c": {
                        "d": true
                      }
                    }
                  },
                  "g": [1, 2, 3]
                }
            }
        """.trimIndent()
        )
    }
}