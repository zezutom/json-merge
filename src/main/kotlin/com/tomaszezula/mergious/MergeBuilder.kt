package com.tomaszezula.mergious

import com.tomaszezula.mergious.strategy.CombineMergeStrategy
import com.tomaszezula.mergious.strategy.JsonPatchMergeStrategy
import com.tomaszezula.mergious.strategy.MergeStrategy
import com.tomaszezula.mergious.strategy.ReplaceMergeStrategy

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

    override fun merge(base: String, other: String?): MergeResult =
        strategy.merge(base.toJson(), other.toJson())
}