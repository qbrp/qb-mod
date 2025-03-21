package org.qbrp.core.components

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.qbrp.core.game.items.components.meta.ItemRender
import org.qbrp.core.resources.data.Data

open class DataComponent(
    var type: String = "",
    var data: JsonElement = Gson().toJsonTree("")
) {
    companion object {
        const val PACKAGE = "org.qbrp.core.game.items.components"
        val GSON = Gson()
    }

    inline fun <reified T> cast(): T {
        val componentClass = try {
            Class.forName("$PACKAGE.$type")
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown component type: $type")
        }

        return GSON.fromJson(data, componentClass) as T
    }

    fun toFile(): String = GSON.toJson(this)

}