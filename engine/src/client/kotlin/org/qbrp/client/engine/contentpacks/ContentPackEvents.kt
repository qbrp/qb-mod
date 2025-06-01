package org.qbrp.client.engine.contentpacks

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface ContentPackEvents {
    fun onApply(pack: ClientContentPack)

    companion object {
        val ON_APPLY: Event<ContentPackEvents> = EventFactory.createArrayBacked(
            ContentPackEvents::class.java
        ) { listeners: Array<out ContentPackEvents> ->
            ContentPackEvents { pack ->
                for (listener in listeners) {
                    listener.onApply(pack)
                }
            }
        }
    }
}