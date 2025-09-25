package org.qbrp.main.core.components

interface InstantiationContext<T: ComponentManager> {
    val componentManager: T
}