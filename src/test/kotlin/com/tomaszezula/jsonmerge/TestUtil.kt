package com.tomaszezula.jsonmerge

import io.kotest.assertions.fail
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beEmpty
import org.json.JSONObject

fun verifySuccess(mergeResult: MergeResult, expectedOutput: String) = when (mergeResult) {
    is Success -> {
        when (val jsonThing = mergeResult.json) {
            is JsonObject -> jsonThing.value shouldBeEqualToComparingFields JSONObject(expectedOutput)
            else -> mergeResult.json.print().normalize() shouldBe expectedOutput.normalize()
        }
    }
    is Failure -> fail("Merge operation failed: ${mergeResult.errorMessage}")
}

fun verifyFailure(mergeResult: MergeResult) = when (mergeResult) {
    is Success -> fail("The operation should have failed.")
    is Failure -> mergeResult.errorMessage shouldNot beEmpty()
}

private fun String.normalize(): String =
    this.replace("\n", "").replace(" ", "").trim()