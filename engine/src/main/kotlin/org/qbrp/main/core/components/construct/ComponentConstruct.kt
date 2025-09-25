package org.qbrp.main.core.components.construct

import org.qbrp.main.core.components.AnyComponent

interface ComponentConstruct<T: AnyComponent> {
    fun construct(): Pair<String, T>
}