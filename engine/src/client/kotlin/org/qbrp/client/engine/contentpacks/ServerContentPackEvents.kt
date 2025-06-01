package org.qbrp.client.engine.contentpacks

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.qbrp.main.engine.chat.core.events.ChatFormatEvent

fun interface ServerContentPackEvents {
    fun onApply(pack: ServerPack)

    companion object {
        val ON_APPLY: Event<ServerContentPackEvents> = EventFactory.createArrayBacked(
            ServerContentPackEvents::class.java
        ) { listeners: Array<out ServerContentPackEvents> ->
            ServerContentPackEvents { pack ->
                for (listener in listeners) {
                    listener.onApply(pack)
                }
            }
        }
    }
}