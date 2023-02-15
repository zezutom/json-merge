package com.tomaszezula.jsonmerge.strategy

import com.tomaszezula.jsonmerge.Json
import com.tomaszezula.jsonmerge.MergeResult
import com.tomaszezula.jsonmerge.model.MergeMode

interface MergeStrategy {
    val mergeMode: MergeMode
    fun merge(base: Json, other: Json): MergeResult
}