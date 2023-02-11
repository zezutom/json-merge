package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.*

class ReplaceMergeStrategy : MergeStrategy {
    override fun merge(base: Json, other: Json): MergeResult = tryMerge(other) {
        when (it) {
            is JsonObject -> JsonObject(it.value.removeNulls())
            else -> it
        }
    }
}