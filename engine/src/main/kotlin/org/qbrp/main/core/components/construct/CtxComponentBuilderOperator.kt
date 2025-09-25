package org.qbrp.main.core.components.construct

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.InstantiationContext

interface CtxComponentBuilderOperator<C: InstantiationContext<*>>: ComponentBuilderOperator<C> {
    val componentContext: C

    fun <X : AnyComponent> buildWithCtx(
        builder: ComponentBuilder<X, C> = getBuilder<X>(),
        registry: ComponentRegistry = GlobalContext.get().get(),
        init: ComponentBuilder<X, C>.() -> Unit
    ) {
        builder.ctx = this.componentContext
        super.build(builder, registry, init)
    }
}