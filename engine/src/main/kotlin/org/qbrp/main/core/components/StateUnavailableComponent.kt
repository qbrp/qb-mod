package org.qbrp.main.core.components

interface StateUnavailableComponent: Component<ComponentManager> {
    override var state: ComponentManager
        get() = throw UnsupportedOperationException("DataComponent не поддерживает обращение к состоянию")
        set(value) = throw UnsupportedOperationException("DataComponent не поддерживает обращение к состояию")
}