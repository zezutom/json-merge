package com.tomaszezula.mergious

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

fun verifySuccess(mergeResult: MergeResult, expectedOutput: String) = when (mergeResult) {
    is Success -> mergeResult.json.print().normalize() shouldBe expectedOutput.normalize()
    is Failure -> fail("Merge operation failed: ${mergeResult.errorMessage}")
}

private fun String.normalize(): String =
    this.replace("\n", "").replace(" ", "").trim()