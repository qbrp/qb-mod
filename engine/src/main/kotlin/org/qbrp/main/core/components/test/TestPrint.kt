package org.qbrp.main.core.components.test

import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager

class TestPrint(override var state: ComponentManager) : Component<ComponentManager> {
    fun print(string: String) {
        println(string)
    }
}