package org.qbrp.main.core.components

interface StateEntry {
    var state: ComponentManager
    @Deprecated("Использовать property-access синтаксис") fun requireState(): ComponentManager = state
}