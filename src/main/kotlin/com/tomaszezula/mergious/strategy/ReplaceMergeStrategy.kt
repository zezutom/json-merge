package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*

class ReplaceMergeStrategy : MergeStrategy {
    override fun merge(base: Json, other: Json): MergeResult = when (other) {
        is JsonObject -> Success(JsonObject(other.value.removeNulls()))
        else -> Success(other)
    }
}