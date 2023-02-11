package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*
import org.json.JSONObject

class JsonPatchMergeStrategy : MergeStrategy {

    override fun merge(base: Json, other: Json): MergeResult =
        when (base) {
            is JsonObject -> tryMerge(base) {
                when (other) {
                    is JsonObject -> JsonObject(mergeObject(it.value, other.value))
                    else -> other
                }
            }

            is JsonArray -> tryMerge(other) {
                when (it) {
                    is JsonObject -> JsonObject(it.value.removeNulls())
                    else -> it
                }
            }

            else -> Success(other)
        }

    private fun mergeObject(base: JSONObject, other: JSONObject): JSONObject {
        val baseKeys = base.keySet()
        val otherKeys = other.keySet()
        val merged = JSONObject()

        otherKeys.forEach { key ->
            if (baseKeys.contains(key)) {
                // Merge the common key
                mergeObjectKey(key, base, other, merged)
            } else {
                // Add a new key from the other JSON
                merged.put(key, other[key])
            }
        }

        // Drop null fields resulting from the merge
        val result = merged.removeNulls()

        // Add all fields unique to the base JSON
        baseKeys.filterNot(otherKeys::contains).forEach { key ->
            result.put(key, base[key])
        }
        return result
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
}