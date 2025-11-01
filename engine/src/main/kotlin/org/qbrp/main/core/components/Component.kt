package org.qbrp.main.core.components

import org.qbrp.main.core.components.extensions.remove

interface Component<T : ComponentManager> : DtoProvider {
    val shouldSave: Boolean get() = true
    val state: T

    fun remove() {
        state.remove(this::class)
    }
}

typealias AnyComponent = Component<*>