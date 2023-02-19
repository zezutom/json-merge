package com.tomaszezula.jsonmerge.strategy

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.tomaszezula.jsonmerge.*
import com.tomaszezula.jsonmerge.model.MergeMode
import org.json.JSONObject

class ReplaceMergeStrategy : MergeStrategy {

    override val mergeMode: MergeMode = MergeMode.REPLACE

    override fun merge(base: Json, other: Json): MergeResult = tryMerge(other) {
        when (it) {
            is JsonObject -> JsonObject(it.value.removeNulls())
            else -> it
        }
    }

    // Find `other` in the `base`, replace it and return the updated base.
    fun replace(jsonPath: String, base: Json, other: Any): MergeResult = when (base) {
        is JsonObject -> {
            try {
                val documentContext = JsonPath.using(Configuration.defaultConfiguration()).parse(base.print())
                val updatedContent = documentContext.map(jsonPath, { _, _ ->
                    val replacement = when (other) {
                        is String -> other.toJson()
                        else -> other
                    }
                    when (replacement) {
                        is JsonArray -> replacement.value
                        is JsonObject -> replacement.value
                        is JsonString -> replacement.value
                        is JsonNull -> null
                        else -> replacement
                    }
                })
                val updatedObject = JSONObject(updatedContent.json<Map<String, Any>>())
                Success(JsonObject(updatedObject))
            } catch (_: PathNotFoundException) {
                Success(base)
            } catch (t: Throwable) {
                Failure("Operation failed", t)
            }
        }

        else -> Failure("Only JSON objects are supported!")
    }
}
