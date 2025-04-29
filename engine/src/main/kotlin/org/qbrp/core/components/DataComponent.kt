package org.qbrp.core.components

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.JsonParseException

open class DataComponent(
    var type: String = "",
    var data: Any? = null,
) {
    companion object {
        const val PACKAGE = "org.qbrp.core.game.items.components"
        val MAPPER = ObjectMapper().apply { registerKotlinModule() }
    }

    fun getName() = type.split("#").getOrNull(1) ?: type
    fun getClassName() = type.split("#")[0]

    inline fun <reified T> cast(): T {
        val componentClass = try {
            Class.forName("$PACKAGE.${getClassName()}") as Class<T>
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown component type: $type")
        }

        return MAPPER.convertValue<T>(data, componentClass) as T
    }

    fun toFile(): String = MAPPER.writeValueAsString(this)

}