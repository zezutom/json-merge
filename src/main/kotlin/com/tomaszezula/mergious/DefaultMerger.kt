package com.tomaszezula.mergious

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DefaultMerger : Merger {
    override fun merge(base: String, other: String?): MergeResult =
        merge(toJson(base), other?.let { toJson(it) } ?: JsonNull)

    private fun toJson(text: String?): Json =
        text?.let { value ->
            value.parseAs(::JSONObject)?.let { JsonObject(it) }
                ?: value.parseAs(::JSONArray)?.let { JsonArray(it) }
                ?: JsonString(value)
        } ?: JsonNull


    private fun <T> String.parseAs(p: (String) -> T): T? {
        return try {
            p(this)
        } catch (_: JSONException) {
            null
        }
    }

    private fun merge(base: Json, other: Json): MergeResult =
        when (base) {
            is JsonObject -> when (other) {
                is JsonObject -> Success(JsonObject(mergeObject(base.value, other.value)))
                else -> Failure("Not a JSON object: $other")
            }

            is JsonArray -> when (other) {
                is JsonArray -> mergeArray(base, other)
                else -> Failure("Not a JSON array: $other")
            }

            is JsonString -> Success(other)
            is JsonNull -> Success(other)
            else -> Failure("Unsupported JSON: $base")
        }

    private fun mergeObject(base: JSONObject, other: JSONObject): JSONObject {
        val baseKeys = base.keySet()
        val otherKeys = other.keySet()
        val jsonObject = JSONObject()
        otherKeys.filter { other[it] != JSONObject.NULL }.forEach { key ->
            if (baseKeys.contains(key)) {
                // Merge the common key
                mergeObjectKey(other, base, jsonObject, key)
            } else {
                // Add a new key from the other JSON
                jsonObject.put(key, other[key])
            }
        }
        // Add all other nodes unique to the base JSON
        baseKeys.filterNot { otherKeys.contains(it) }.forEach { key ->
            jsonObject.put(key, base[key])
        }
        return jsonObject
    }

    private fun mergeObjectKey(
        other: JSONObject,
        base: JSONObject,
        jsonObject: JSONObject,
        key: String
    ) {
        other[key]?.let { value ->
            when (value) {
                is JSONObject -> {
                    when (val baseValue = base[key]) {
                        is JSONObject -> jsonObject.put(
                            key,
                            mergeObject(baseValue, value)
                        )

                        else -> jsonObject.put(key, value)
                    }
                }

                else -> jsonObject.put(key, value)
            }
        } ?: jsonObject.put(key, base[key])
    }

    private fun mergeArray(base: JsonArray, other: JsonArray): MergeResult {
        TODO()
    }

    private fun mergeString(base: JsonString, other: String?): MergeResult {
        TODO()
    }
}