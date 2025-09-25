package org.qbrp.main.core.keybinds

import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.EventEnv
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.players.model.ServerPlayerObject

context(eventCtx: EventContext)
fun EventEnv.onKeybind(id: KeybindId, method: (ServerPlayerObject) -> Unit) {
    on<KeybindCallback>(id.toEventId()) { method(it.player) }
}