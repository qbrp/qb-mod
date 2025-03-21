package org.qbrp.core.resources.content

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.qbrp.core.components.DataComponent
import org.qbrp.core.resources.data.Data
import java.util.concurrent.CompletableFuture

data class ItemConfig(
    val parent: String = "abstract_generated",
    val name: String,
    val components: List<DataComponent>,
    val tags: List<Tag>
) : Data() {
    override fun toFile(): String = gson.toJson(this)

    fun suggestItemTag(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        tags.forEach { builder.suggest(it.name) }
        return builder.buildFuture()
    }

    fun getTag(tag: String): Tag = tags.find { it.name == tag } ?: tags.get(0)

    data class Tag(val name: String, val components: List<DataComponent>)
}