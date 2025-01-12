package org.qbrp.plasmo.model.priority

import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.server.command.ServerCommandSource

object Priorities {
    private val list = mutableListOf<Priority>(
        Priority("player"),
        Priority("world")
    )
    fun getPriority(name: String): Priority? = list.find { it.name == name }
    fun getIndex(priority: Priority): Int = list.indexOf(priority)
    fun lowest() = list.last()
    fun highest() = list.first()
    fun fromStrings(strings: List<String>) {
        this.list.clear()
        strings.forEach { list.add(Priority(it)) }
    }

    val prioritySuggestions: SuggestionProvider<ServerCommandSource> = SuggestionProvider { context, builder ->
        list.forEach { priority ->
            builder.suggest(priority.name)
        }
        builder.buildFuture()
    }
}