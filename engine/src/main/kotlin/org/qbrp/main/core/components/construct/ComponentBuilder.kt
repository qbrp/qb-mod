package org.qbrp.main.core.components.construct

import org.jetbrains.annotations.ApiStatus
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.core.components.extensions.getOrAdd

class ComponentBuilder<T: AnyComponent, C: InstantiationContext<*>> {
    var name: String? = null
    @ApiStatus.Internal var ctx: C? = null
    lateinit var _factory: (C) -> T
    private var onAddedAction: ((T) -> Unit)? = null
    private var handleAction: ((T) -> Unit)? = null

    fun onAdded(action: (T) -> Unit) {
        onAddedAction = action
    }

    fun handle(action: (T) -> Unit) {
        handleAction = action
    }

    inline fun <reified K: T> factory(noinline f: (C) -> K) {
        _factory = f as (C) -> T
        name = GlobalContext.get().get<ComponentRegistry>().getEntry(K::class).id
    }

    internal fun buildAndAdd(state: ComponentManager) {
        val component1 = state.getComponentAs<T>(name!!)
        if (component1 != null) {
            handleAction?.invoke(component1)
            return
        }
        val component = _factory(ctx!!)
        state.getOrAdd<AnyComponent>(name!!) { component }
        onAddedAction?.invoke(component)
    }
}
