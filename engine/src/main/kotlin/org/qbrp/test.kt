package org.qbrp

import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

@Target(AnnotationTarget.FUNCTION)
annotation class Event(val id: String)

object EventManager {
    private val listeners = mutableMapOf<String, MutableList<Pair<Any, KFunction<*>>>>()

    fun registerEvent(listener: Any) {
        listener::class.functions.forEach { function ->
            function.annotations.filterIsInstance<Event>().forEach { event ->
                listeners.getOrPut(event.id) { mutableListOf() }.add(listener to function)
            }
        }
    }

    fun callEvent(id: String, args: Map<String, Any?>) {
        listeners[id]?.forEach { (listener, function) ->
            val params = function.parameters
            val arguments = mutableListOf<Any?>()
            if (params.firstOrNull()?.name == null) {
                arguments.add(listener)
            }
            val allArgsPresent = params.drop(1).all { param ->
                val arg = args[param.name]
                arg != null || param.isOptional
            }

            if (!allArgsPresent) {
                println("Skipping function ${function.name}: not all required arguments are provided.")
                return
            }
            params.drop(1).forEach { param ->
                arguments.add(args[param.name] ?: param)
            }

            function.isAccessible = true
            try {
                function.call(*arguments.toTypedArray())
            } catch (e: IllegalArgumentException) {
                println("Failed to invoke function ${function.name} due to argument mismatch: ${e.message}")
            }
        }
    }
}

class EventHandlers {
    @Event(id = "RECORD_ACTION")
    fun logEvent(message: String) {
        println("Log: $message")
    }

    @Event(id = "RECORD_ACTION")
    fun logEvent(message: String, author: String = "") {
        println("Log by $author: $message")
    }
}

fun main() {
    // Регистрация обработчиков
    val handlers = EventHandlers()
    EventManager.registerEvent(handlers)

    // Вызов событий
    EventManager.callEvent("RECORD_ACTION", mapOf("message" to "Test message"))
    EventManager.callEvent("RECORD_ACTION", mapOf("message" to "Test message", "author" to "Alice"))
    EventManager.callEvent("RECORD_ACTION", mapOf("message" to "Test message", "author" to ""))
    EventManager.callEvent("RECORD_ACTION", mapOf("message" to "Test message", "nonexistent" to "value"))
}
