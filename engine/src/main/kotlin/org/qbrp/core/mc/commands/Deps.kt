package org.qbrp.core.mc.commands

class Deps(private val dependencies: Map<String, Any> = emptyMap()) {
    fun get(name: String): Any {
        return dependencies[name]
            ?: throw IllegalArgumentException("Зависимость с именем '$name' не найдена")
    }
}
