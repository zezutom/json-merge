package com.tomaszezula.mergious

import org.json.JSONArray
import org.json.JSONObject

interface Json {
    fun prettyPrint(): String

    fun print(): String
}

@JvmInline
value class JsonArray(val value: JSONArray) : Json {
    override fun prettyPrint(): String = value.toString(3)

    override fun print(): String = value.toString()
}

@JvmInline
value class JsonObject(val value: JSONObject) : Json {
    override fun prettyPrint(): String = value.toString(3)

    override fun print(): String = value.toString()
}
@JvmInline
value class JsonString(private val value: String) : Json {
    override fun prettyPrint(): String = print()

    override fun print(): String = value
}

object JsonNull : Json {
    override fun prettyPrint(): String = print()

    override fun print(): String = "null"
}