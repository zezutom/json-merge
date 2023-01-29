package com.tomaszezula.mergious

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun String?.toJson(): Json =
    this?.let { value ->
        value.parseAs(::JSONObject)?.let { JsonObject(it) }
            ?: value.parseAs(::JSONArray)?.let { JsonArray(it) }
            ?: if (value == DefaultMerger.NullValue) JsonNull else JsonString(value)
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

private fun <T> String.parseAs(p: (String) -> T): T? {
    return try {
        p(this)
    } catch (_: JSONException) {
        null
    }
}
