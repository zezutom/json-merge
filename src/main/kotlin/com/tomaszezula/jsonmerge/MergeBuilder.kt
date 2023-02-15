package com.tomaszezula.jsonmerge

import com.tomaszezula.jsonmerge.model.MergeMode
import com.tomaszezula.jsonmerge.strategy.CombineMergeStrategy
import com.tomaszezula.jsonmerge.strategy.JsonPatchMergeStrategy
import com.tomaszezula.jsonmerge.strategy.MergeStrategy
import com.tomaszezula.jsonmerge.strategy.ReplaceMergeStrategy

class MergeBuilder {
    private var strategy: MergeStrategy = JsonPatchMergeStrategy()

    fun withCombineMode(): MergeBuilder {
        strategy = CombineMergeStrategy()
        return this
    }

    fun withJsonPatchMode(): MergeBuilder {
        strategy = JsonPatchMergeStrategy()
        return this
    }

    fun withReplaceMode(): MergeBuilder {
        strategy = ReplaceMergeStrategy()
        return this
    }

    fun build(): Merger =
        DefaultMerger(strategy)
}

private class DefaultMerger(private val strategy: MergeStrategy) : Merger {
    override val mergeMode: MergeMode = strategy.mergeMode

    override fun merge(base: String, other: String?): MergeResult =
        strategy.merge(base.toJson(), other.toJson())
}