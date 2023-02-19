package com.tomaszezula.jsonmerge

import com.tomaszezula.jsonmerge.model.MergeMode

interface Merger {
    val mergeMode: MergeMode

    fun merge(base: String, other: String?): MergeResult

    fun merge(jsonPath: String, base: String, other: Any): MergeResult
}
