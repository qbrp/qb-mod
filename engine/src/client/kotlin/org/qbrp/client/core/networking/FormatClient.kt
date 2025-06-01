package org.qbrp.client.core.networking

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.text.Text

object FormatClient {

    fun Component.native(): Text {
        val json = GsonComponentSerializer.gson().serializer().toJson(this)
        return Text.Serializer.fromJson(json)!!
    }
}