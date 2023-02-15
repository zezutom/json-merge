package com.tomaszezula.jsonmerge.strategy

import com.tomaszezula.jsonmerge.*
import com.tomaszezula.jsonmerge.model.MergeMode

class ReplaceMergeStrategy : MergeStrategy {

    override val mergeMode: MergeMode = MergeMode.REPLACE

    override fun merge(base: Json, other: Json): MergeResult = tryMerge(other) {
        when (it) {
            is JsonObject -> JsonObject(it.value.removeNulls())
            else -> it
        }
    }
}