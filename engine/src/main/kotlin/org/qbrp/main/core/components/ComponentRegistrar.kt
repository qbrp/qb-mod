package org.qbrp.main.core.components

import org.qbrp.main.core.components.ComponentRegistry.ComponentEntryBuilder
import kotlin.reflect.KClass

class ComponentRegistrar(private val registry: ComponentRegistry) {
    fun use(componentKClass: KClass<out AnyComponent>) {
        registry.register { componentClass(componentKClass) }
    }
    inline fun <reified C: AnyComponent> use() {
        use(C::class)
    }
    fun build(builder: (ComponentEntryBuilder).() -> Unit) {
        registry.register(builder)
    }
}