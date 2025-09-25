package core.keybind

import core.TestEnv
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.events.component.ComponentEventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.keybinds.KeybindCallback
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.engine.players.model.ServerPlayerObject

class KeybindTest: TestEnv() {
    private lateinit var eventContext: ComponentEventContext

    @BeforeEach
    fun setup() {
        eventContext = newEventContext()
    }

    @Test
    fun keybindAcceptsAndInvokeListeners() {
        val id = KeybindId("test")
        var invoked = false
        with(eventContext) {
            onKeybind(id) { invoked = true }
            KeybindCallback(mockk<ServerPlayerObject>(relaxed = true)).emit(id.toEventId())
        }
        assert(invoked) { "Событие не обработано" }
    }
}