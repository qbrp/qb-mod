package org.qbrp.client.core.keybinds

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.AbstractCancellableEvent
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventBus
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.EventEnv
import org.qbrp.main.core.events.EventHandler
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.EventListener
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.on
import org.qbrp.main.core.keybinds.KeybindCallback
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class KeyPressEvent(val key: Int) : Event

data class KeyReleaseEvent(val key: Int) : Event

data class KeyHoldEvent(val key: Int) : Event

data class KeyEvent(val key: Int, val action: Int) : Event

class KeyBindEvent : AbstractCancellableEvent()

context(event: EventContext)
fun EventEnv.onKeybind(
    keybindId: KeybindId,
    priority: Int = EventPriority.BASE,
    handler: EventHandler<KeyBindEvent, Unit>
) {
    on(keybindId.toEventId(), priority, handler)
}

context(event: EventContext)
fun EventEnv.onAnyKeybind(keybinds: List<KeybindId>, method: () -> Unit) {
    keybinds.forEach {
        onKeybind(it) { method() }
    }
}
