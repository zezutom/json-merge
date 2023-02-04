package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*
import org.json.JSONArray
import org.json.JSONObject

class CombineMergeStrategy : MergeStrategy {
    companion object {
        const val MergedField = "merged"
    }
    override fun merge(base: Json, other: Json): MergeResult =
        when (base) {
            is JsonObject -> when (other) {
                is JsonObject -> tryMerge(base.value) { JsonObject(mergeObject(base.value, other.value)) }
                else -> Success(base.add(MergedField, other))
            }

            is JsonArray -> when (other) {
                is JsonArray -> tryMerge(base.value) { JsonArray(mergeArray(base.value, other.value)) }
                else -> Success(base.append(other))
            }

            is JsonString, is JsonNull -> Success(other)
            else -> Failure("Unsupported JSON: $base")
        }

    private fun mergeObject(base: JSONObject, other: JSONObject): JSONObject {
        val baseKeys = base.keySet()
        val otherKeys = other.keySet()

        val result = JSONObject()

        otherKeys.forEach { key ->
            if (baseKeys.contains(key)) {
                when (val baseValue = base[key]) {
                    is JSONObject -> when (val otherValue = other[key]) {
                        is JSONObject -> mergeObject(baseValue, otherValue)
                        else -> result.put(key, baseValue.add(MergedField, otherValue))
                    }
                    is JSONArray -> result.put(key, baseValue.append(other[key]))
                    else -> result.put(key, other[key])
                }
            } else {
                result.put(key, other[key])
            }
        }

        // Add all fields unique to the base JSON
        baseKeys.filterNot(otherKeys::contains).forEach { key ->
            result.put(key, base[key])
        }

        return result
    }

    private fun mergeArray(base: JSONArray, other: JSONArray): JSONArray {
        val result = JSONArray()
        base.forEach(result::put)
        other.forEach(result::put)

        return result
    }
}