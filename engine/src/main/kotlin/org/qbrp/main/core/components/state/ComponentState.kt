package org.qbrp.main.core.components.state

import org.qbrp.main.core.components.AssociatedComponentState

open class ComponentState<T: Any>() : ComponentHolder(), AssociatedComponentState<T> {
    override var parent: T
        get() = _parent ?: throw Exception("Родитель не был назначен")
        set(value) { _parent = value }
    private var _parent: T? = null

}