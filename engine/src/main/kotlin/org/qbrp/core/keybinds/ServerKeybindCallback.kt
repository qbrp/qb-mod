package org.qbrp.core.keybinds

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage


fun interface ServerKeybindCallback {
    fun onKeyPress(player: ServerPlayerEntity): ActionResult

    companion object {
        private val EVENT_MAP: MutableMap<String, Event<ServerKeybindCallback>> = mutableMapOf()

        fun getOrCreateEvent(id: String): Event<ServerKeybindCallback> {
            return EVENT_MAP.getOrPut(id) {
                EventFactory.createArrayBacked(
                    ServerKeybindCallback::class.java,
                    { listeners: Array<out ServerKeybindCallback> ->
                        ServerKeybindCallback { player ->
                            var result = ActionResult.PASS
                            for (listener in listeners) {
                                val listenerResult = listener.onKeyPress(player)
                                if (listenerResult.isAccepted) {
                                    result = listenerResult
                                }
                            }
                            result
                        }
                    }
                )
            }
        }
    }
}