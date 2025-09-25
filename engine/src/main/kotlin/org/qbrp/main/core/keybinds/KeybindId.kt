package org.qbrp.main.core.keybinds

import org.qbrp.main.core.events.EventId

@JvmInline
value class KeybindId(val value: String) {
    fun toEventId() = EventId("keybind.$value")
}