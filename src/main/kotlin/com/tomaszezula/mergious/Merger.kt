package com.tomaszezula.mergious

interface Merger {

    fun merge(base: String, other: String?): MergeResult
}
