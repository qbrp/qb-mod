package org.qbrp.main.core.components.construct

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.core.components.extensions.getOrAdd
import kotlin.reflect.KClass

interface ComponentBuilderOperator<C: InstantiationContext<*>>: ComponentManager {
    companion object {
        fun <C : InstantiationContext<*>> ComponentManager.operator(): ComponentBuilderOperator<C> {
            return object : ComponentBuilderOperator<C>, ComponentManager by this {}
        }

        inline fun <reified X: AnyComponent> ComponentBuilderOperator<*>.default(registry: ComponentRegistry = GlobalContext.get().get(),): X {
            return getOrAdd<X> { registry.getEntry(X::class).requireConstructor().invoke(this@default) as X }
        }

        fun ComponentBuilderOperator<*>.default(klass: KClass<out AnyComponent>, registry: ComponentRegistry = GlobalContext.get().get(),): AnyComponent {
            return getOrAdd { registry.getEntry(klass).requireConstructor().invoke(this@default) }
        }

        fun ComponentBuilderOperator<*>.default(klass: KClass<out AnyComponent>, registry: ComponentRegistry = GlobalContext.get().get(), init: ComponentBuilder<*, *>.() -> Unit) {
            build {
                init()
                factory {
                    registry.getEntry(klass).requireConstructor().invoke(this@default)
                }
            }
        }
    }

    fun <X: AnyComponent> getBuilder(): ComponentBuilder<X, C> {
        return ComponentBuilder<X, C>()
    }

    fun <X: AnyComponent> build(builder: ComponentBuilder<X, C> = getBuilder<X>(),
                                registry: ComponentRegistry = GlobalContext.get().get(),
                                init: ComponentBuilder<X, C>.() -> Unit) {
        builder.init()
        builder.buildAndAdd(this)
    }
}