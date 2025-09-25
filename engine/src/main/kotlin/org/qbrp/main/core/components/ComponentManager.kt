package org.qbrp.main.core.components

import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent.inject
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.core.components.extensions.copy
import org.qbrp.main.core.components.serialization.ComponentJsonField
import org.qbrp.main.core.components.serialization.SaveListener
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.core.utils.log.LoggerUtil
import java.util.Spliterator
import kotlin.reflect.KClass

interface ComponentManager: Iterable<AnyComponent> {
    override fun iterator(): Iterator<AnyComponent> = getComponents().iterator()
    override fun spliterator(): Spliterator<AnyComponent> = getComponents().spliterator()

    fun <T: AnyComponent> addComponent(component: T, name: String, enable: Boolean = false, putState: Boolean = true): T

    fun removeComponent(component: AnyComponent): AnyComponent?

    fun getComponent(name: String): AnyComponent?

    fun getComponents(): List<AnyComponent>

    fun removeAll() = forEach { removeComponent(it) }

    fun namedIterator(): Iterator<Pair<Name, AnyComponent>>

    val names: List<String> get() = namedIterator().asSequence().map { it.first.full }.toList()

    fun enableAll() {
        forEach { if (it is Activateable) it.enable() }
    }

    fun removeComponent(name: String): AnyComponent? {
        namedIterator().forEach { (n, c) ->
            if (n.full == name) {
                removeComponent(c)
                return c
            }
        }
        return null
    }

    fun <T> getComponentAs(name: String): T? {
        return getComponent(name) as? T?
    }

    fun <T : AnyComponent> addComponentIfNotExist(name: String, factory: () -> T, enable: Boolean = false) {
        getComponentOrAdd<T>(name, factory, enable)
    }

    fun <T: AnyComponent> getComponentOrAdd(name: String, factory: () -> T, enable: Boolean): T {
        getComponentAs<T>(name)?.let { return it }
        return addComponent(factory(), name = name, enable = enable) as T
    }

    fun <T : AnyComponent> replaceComponent(name: String, factory: () -> T): T? {
        val old = removeComponent(name)
        return if (old != null) {
            addComponent(factory(), name) as T
        } else null
    }

    fun requireComponentByName(name: String): AnyComponent {
        return getComponent(name) ?: throw ComponentNotFoundException("Компонент $name не найден")
    }
}