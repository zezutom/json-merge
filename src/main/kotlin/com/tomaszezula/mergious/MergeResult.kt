package com.tomaszezula.mergious

sealed interface MergeResult

@JvmInline
value class Failure(val errorMessage: String) : MergeResult
@JvmInline
value class Success(val json: Json) : MergeResult