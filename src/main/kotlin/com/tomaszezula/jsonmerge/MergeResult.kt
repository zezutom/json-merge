package com.tomaszezula.jsonmerge

sealed interface MergeResult {
    fun getOrThrow(prettyPrint: Boolean = false): String
}

data class Failure(val errorMessage: String, val throwable: Throwable? = null) : MergeResult {
    override fun getOrThrow(prettyPrint: Boolean): String =
        throwable?.let { throw it } ?: throw IllegalStateException(errorMessage)
}

@JvmInline
value class Success(val json: Json) : MergeResult {
    override fun getOrThrow(prettyPrint: Boolean): String =
        if (prettyPrint) json.prettyPrint() else json.print()
}