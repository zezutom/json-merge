package com.tomaszezula.jsonmerge

sealed interface MergeResult

data class Failure(val errorMessage: String, val throwable: Throwable? = null) : MergeResult
@JvmInline
value class Success(val json: Json) : MergeResult