package org.qbrp.core.resources.content

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.qbrp.core.resources.parsing.ParserBuilder
import org.qbrp.core.resources.parsing.filters.ExtensionFilter
import org.qbrp.core.resources.structure.Structure
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.system.utils.keys.Key
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.io.path.nameWithoutExtension

class ItemStorage(path: Path): Structure(path.toFile()) {

    init {
        openItems()
    }

    fun openItems() {
        val configs = ParserBuilder()
            .setClass(ItemConfig::class.java)
            .addFilter(ExtensionFilter(setOf("json")))
            .build()
                .parse(path.toFile())
                .map { (it as ContentUnit) }
        for (config in configs) {
            registerContent(config, Key((config.data as ItemConfig).name))
        }
    }

    fun getItem(name: String): ItemConfig = content(Key(name)).data as ItemConfig

    fun getTag(name: String, tag: String): ItemConfig.Tag = getItem(name).tags.find { it.name == tag } ?: getItem(name).tags.get(0)

    fun listItems(): List<String> = contentRegistry.keys.map { it.name }

    fun listItemTags(item: String): List<String> = getItem(name).tags.map { it.name }

    fun suggestItems(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        listItems().forEach { builder.suggest(it) }
        return builder.buildFuture()
    }

}