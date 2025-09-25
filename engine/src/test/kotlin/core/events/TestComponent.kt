package core.events

import org.qbrp.main.core.components.DataComponent

class TestComponent(var invoked: Boolean): DataComponent {
    fun invoke() {
        invoked = true
    }
}