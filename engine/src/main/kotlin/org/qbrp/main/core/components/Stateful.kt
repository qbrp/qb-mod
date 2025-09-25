package org.qbrp.main.core.components

interface Stateful<T: ComponentManager> {
    val state: T
}