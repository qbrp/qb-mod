package org.qbrp.client.controls

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.CancellableEvent
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.EventHandler
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.keybind.KeybindId

data class KeyPressEvent(val key: Int) : Event

data class KeyReleaseEvent(val key: Int) : Event

data class KeyHoldEvent(val key: Int) : Event

data class KeyEvent(val key: Int, val action: Int) : Event

class KeyBindEvent : CancellableEvent()

fun onKeybind(
    keybindId: KeybindId,
    priority: Int = EventPriority.BASE,
    handler: EventHandler<KeyBindEvent, Unit>
) {
    on(EventType<KeyBindEvent>(keybindId.toEventId()), priority = priority, handler = handler)
}

context(event: EventContext)
fun onAnyKeybind(keybinds: List<KeybindId>, method: () -> Unit) {
    keybinds.forEach {
        onKeybind(it) { method() }
    }
}


object KeyboardEventsMixinAccess : KoinComponent {
    fun emitKeyPressEvent(key: Int): Boolean {
        return KeyPressEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyReleaseEvent(key: Int): Boolean {
        return KeyReleaseEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyRepeatEvent(key: Int): Boolean {
        return KeyHoldEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyEvent(key: Int, action: Int): Boolean {
        return KeyEvent(key, action).collect<Boolean>().any { it }
    }
}