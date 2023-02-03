package com.tomaszezula.mergious.strategy

import com.tomaszezula.mergious.Json
import com.tomaszezula.mergious.MergeResult

interface MergeStrategy {
    fun merge(base: Json, other: Json): MergeResult
}