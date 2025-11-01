package org.qbrp.main.engine.keybind

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class KeybindCallback(val player: ServerPlayerObject) : Event

@JvmInline
value class KeybindId(val value: String) {
    fun toEventId() = EventId("keybind.$value")
}

fun emulateKeybind(player: ServerPlayerObject, id: KeybindId) {
    KeybindCallback(player).emit(EventType<KeybindCallback>(id.toEventId()))
}

fun onKeybind(id: KeybindId, method: (ServerPlayerObject) -> Unit) {
    on(EventType<KeybindCallback>(id.toEventId())) { method(player) }
}