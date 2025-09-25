package org.qbrp.main.core.components.objects

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.test.TestPrint

class TestObject(state: ComponentManager): StateObject<ComponentManager>(state) {
    init {
        state.add(TestPrint(state))
    }
}