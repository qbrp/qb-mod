package org.qbrp.main.engine.music.plasmo.model.priority

import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.server.command.ServerCommandSource

class Priorities {
    private val list = mutableListOf<Priority>()
    fun getPriority(name: String): Priority? = list.find { it.name == name }
    fun getIndex(priority: Priority): Int {
        val priorityValue = priority.name.split("-").first() // Извлекаем только число до дефиса
        return priorityValue.toIntOrNull() ?: Int.MAX_VALUE // Преобразуем в число или возвращаем максимально возможное значение, если не удалось преобразовать
    }
    fun lowest() = list.last()
    fun highest() = list.first()
    fun fromStrings(strings: List<String>) {
        this.list.clear()
        strings.forEach { list.add(Priority(it)) }
    }
    fun getAll() = list
}