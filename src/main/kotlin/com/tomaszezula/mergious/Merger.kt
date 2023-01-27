package com.tomaszezula.mergious

/**
 * TODO https://www.rfc-editor.org/rfc/rfc7396
 */
interface Merger {

    fun merge(base: String, other: String?): MergeResult
}