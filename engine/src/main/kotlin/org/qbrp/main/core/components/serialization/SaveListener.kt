package org.qbrp.main.core.components.serialization

import org.qbrp.main.core.components.ComponentManager

interface SaveListener {
    fun onSave(state: ComponentManager)
}