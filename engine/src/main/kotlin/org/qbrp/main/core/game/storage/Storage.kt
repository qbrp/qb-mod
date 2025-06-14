package org.qbrp.main.core.game.storage

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.serialization.Identifiable

interface Storage<T : Identifiable>: KoinComponent, ObjectProvider<T> {
    fun add(obj: T): T
    fun remove(key: String): Boolean
    fun getAll(): Collection<T>
    fun enableComponent(component: Component, ) {
        getAll()
            .filterIsInstance<Stateful>()
            .forEach { (it.state.getComponentByName(get<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.enable() }
    }
    fun disableComponent(component: Component, ) {
        getAll()
            .filterIsInstance<Stateful>()
            .forEach { (it.state.getComponentByName(get<ComponentsRegistry>().getComponentName(component)) as? Behaviour)?.disable() }
    }
    fun putIfAbsent(obj: T): T? {
        val existing = getAll().find { it.id == obj.id }
        return if (existing == null) {
            add(obj)
            null
        } else {
            existing
        }
    }
    fun clear()
}
