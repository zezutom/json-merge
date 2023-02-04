package com.tomaszezula.mergious

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val NULL_VALUE = "null"

fun String?.toJson(): Json =
    this?.let { value ->
        value.parseAs(::JSONObject)?.let { JsonObject(it) }
            ?: value.parseAs(::JSONArray)?.let { JsonArray(it) }
            ?: if (value == NULL_VALUE) JsonNull else JsonString(value)
    } ?: JsonNull

fun JSONObject.removeNulls(): JSONObject {
    val result = JSONObject()
    this.keySet().filter { this[it] != JSONObject.NULL }.forEach { key ->
        when (val value = this[key]) {
            is JSONObject -> result.put(key, value.removeNulls())
            else -> result.put(key, value)
        }
    }
    return result
}

fun JSONObject.add(key: String, other: Any): JSONObject {
    val result = JSONObject(this, *this.keySet().toTypedArray())
    result.put(key, other)
    return result
}

fun JSONArray.append(other: Any): JSONArray {
    val result = JSONArray().putAll(this)
    when (other) {
        is JSONArray -> result.putAll(other)
        else -> result.put(other)
    }
    return result
}

fun JsonObject.add(propertyName: String, other: Json): JsonObject {
    val keys = this.value.keySet()
    var newKey = propertyName
    var i = 0

    while (keys.contains(newKey)) {
        newKey = "${newKey}${i++}"
    }

    val newValue = JSONObject()
    this.value.keys().forEach { newValue.put(it, this.value.get(it)) }
    newValue.put(
        newKey, when (other) {
            is JsonObject -> other.value
            is JsonArray -> other.value
            is JsonString -> other.value
            else -> other
        }
    )
    return JsonObject(newValue)
}

fun JsonArray.append(other: Json): JsonArray {
    val newValue = JSONArray()
    this.value.forEach(newValue::put)
    when (other) {
        is JsonObject -> newValue.put(other.value)
        is JsonArray -> newValue.put(other.value)
        is JsonString -> newValue.put(other.value)
        else -> newValue.put(other)
    }
    return JsonArray(newValue)
}

fun <T, R : Json> tryMerge(base: T, f: (T) -> R): MergeResult =
    try {
        Success(f(base))
    } catch (t: Throwable) {
        Failure("Merge failed", t)
    }

private fun <T> String.parseAs(p: (String) -> T): T? {
    return try {
        p(this)
    } catch (_: JSONException) {
        null
    }
}