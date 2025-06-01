package org.qbrp.main.core.info

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer


fun interface ServerInformationGetEvent {
    fun event(viewer: ClusterViewer)

    companion object {
        val EVENT: Event<ServerInformationGetEvent> = EventFactory.createArrayBacked(
            ServerInformationGetEvent::class.java,
            { listeners: Array<out ServerInformationGetEvent> ->
                ServerInformationGetEvent {
                    for (listener in listeners) {
                        listener.event(it)
                    }
                }
            }
        )
    }
}