package org.qbrp.main.core.components.extensions

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.InstantiationContext

class EmptyContext: InstantiationContext<Nothing> {
    override val componentManager: Nothing get() = throw UnsupportedOperationException()
}

class Context<T: ComponentManager>(override val componentManager: T): InstantiationContext<T>

fun emptyCtx() = EmptyContext()
fun <T: ComponentManager> instCtx(m: T) = Context<T>(m)