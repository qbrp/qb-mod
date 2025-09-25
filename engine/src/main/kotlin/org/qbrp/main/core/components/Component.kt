package org.qbrp.main.core.components

import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.game.prefabs.PrefabField

interface Component<T: ComponentManager> : PrefabField, DtoProvider {
    val shouldSave: Boolean get() = true
    val state: T

    fun remove() {
        state.remove(this::class)
    }

    override fun component(): AnyComponent {
        return this
    }
}

typealias AnyComponent = Component<*>