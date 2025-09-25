package org.qbrp.client.core.keybinds

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.allTrue
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext

object KeyboardMixinAccess: KoinComponent {
    fun emitKeyPressEvent(key: Int): Boolean = withEventContext {
        KeyPressEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyReleaseEvent(key: Int): Boolean = withEventContext {
        KeyReleaseEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyRepeatEvent(key: Int): Boolean = withEventContext {
        KeyHoldEvent(key).collect<Boolean>().any { it }
    }

    fun emitKeyEvent(key: Int, action: Int): Boolean = withEventContext {
        KeyEvent(key, action).collect<Boolean>().any { it }
    }
}