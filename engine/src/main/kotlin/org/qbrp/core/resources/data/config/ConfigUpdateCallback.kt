package org.qbrp.core.resources.data.config

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage

fun interface ConfigUpdateCallback {
    fun onConfigUpdated(config: ServerConfigData)

    companion object {
        val EVENT: Event<ConfigUpdateCallback> = EventFactory.createArrayBacked(
            ConfigUpdateCallback::class.java,
            { listeners: Array<out ConfigUpdateCallback> ->
                ConfigUpdateCallback { config ->
                    for (listener in listeners) {
                        listener.onConfigUpdated(config)
                    }
                    ActionResult.SUCCESS
                }
            }
        )
    }
}