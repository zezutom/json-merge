package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONString

class CombineMergeStrategy : MergeStrategy {
    companion object {
        const val MergedField = "merged"
    }

    override fun merge(base: Json, other: Json): MergeResult = when (base) {
        is JsonObject -> tryMerge(base) {
            when (other) {
                is JsonObject -> JsonObject(mergeObject(it.value, other.value))
                is JsonString, is JsonArray -> it.add(MergedField, other)
                else -> it
            }
        }

        is JsonArray -> tryMerge(base) {
            when (other) {
                is JsonArray -> JsonArray(mergeArray(it.value, other.value))
                else -> it.append(other)
            }
        }

        is JsonString -> tryMerge(base) {
            when (other) {
                is JsonObject -> JsonArray(JSONArray(listOf(it.value, other.value)))
                is JsonArray -> JsonArray(other.value.addItem(it.value))
                is JsonString -> JsonArray(JSONArray(listOf(it.value, other.value)))
                else -> it
            }
        }

        else -> Success(base)
    }

    private fun mergeObject(base: JSONObject, other: JSONObject): JSONObject {
        val baseKeys = base.keySet()
        val otherKeys = other.keySet()

        val result = JSONObject()

        otherKeys.forEach { key ->
            if (baseKeys.contains(key)) {
                when (val baseValue = base[key]) {
                    is JSONObject -> when (val otherValue = other[key]) {
                        is JSONObject -> {
                            mergeObject(baseValue, otherValue)
                        }

                        is JSONString -> result.put(key, baseValue.add(MergedField, otherValue))
                        else -> result.put(key, baseValue)
                    }

                    is JSONArray -> {
                        result.put(key, baseValue.append(other[key]))
                    }

                    else -> {
                        val mergedValue = when (val otherValue = other[key]) {
                            is JSONArray -> {
                                otherValue.addItem(baseValue)
                            }

                            else -> JSONArray(listOf(baseValue, otherValue))
                        }
                        result.put(key, mergedValue)
                    }
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