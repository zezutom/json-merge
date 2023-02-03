package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*
import org.json.JSONObject

class JsonPatchMergeStrategy : MergeStrategy {

    override fun merge(base: Json, other: Json): MergeResult =
        when (base) {
            is JsonObject -> when (other) {
                is JsonObject -> tryMerge(base.value) { JsonObject(mergeObject(it, other.value)) }
                is JsonArray, is JsonString, is JsonNull -> Success(other)
                else -> Failure("Unsupported format: $other")
            }

            is JsonArray -> when (other) {
                is JsonObject -> Success(JsonObject(other.value.removeNulls()))
                else -> Success(other)
            }

            is JsonString, is JsonNull -> Success(other)
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
        otherKeys.forEach { key ->
            if (baseKeys.contains(key)) {
                // Merge the common key
                mergeObjectKey(key, base, other, merged)
            } else {
                // Add a new key from the other JSON
                merged.put(
                    key, when (val value = other[key]) {
                        is JSONObject -> value
                        else -> value
                    }
                )
            }
        }
        return merged.removeNulls()
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

    private fun <T, R : Json> tryMerge(base: T, f: (T) -> R): MergeResult =
        try {
            Success(f(base))
        } catch (t: Throwable) {
            Failure("Merge failed", t)
        }
}