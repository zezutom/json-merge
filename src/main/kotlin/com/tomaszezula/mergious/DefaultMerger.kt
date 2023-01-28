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
        val merged = JSONObject()

        // Add all fields unique to the base JSON
        baseKeys.filterNot { otherKeys.contains(it) }.forEach { key ->
            merged.put(key, base[key])
        }

        // Resolve the overlapping fields
        otherKeys.filter { other[it] != JSONObject.NULL }.forEach { key ->
            if (baseKeys.contains(key)) {
                // Merge the common key
                mergeObjectKey(key, base, other, merged)
            } else {
                // Add a new key from the other JSON
                merged.put(key, other[key])
            }
        }
        return merged
    }

    private fun mergeObjectKey(
        key: String,
        base: JSONObject,
        other: JSONObject,
        merged: JSONObject
    ) {
        when (val value = other[key]) {
            is JSONObject -> {
                when (val baseValue = base[key]) {
                    is JSONObject -> merged.put(key, mergeObject(baseValue, value))
                    else -> merged.put(key, value)
                }
            }
            else -> merged.put(key, value)
        }
    }

    private fun mergeArray(base: JsonArray, other: JsonArray): MergeResult {
        TODO()
    }

    private fun mergeString(base: JsonString, other: String?): MergeResult {
        TODO()
    }
}