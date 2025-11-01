package org.qbrp.main.core.components

import org.qbrp.main.core.components.exception.ComponentNotFoundException
import java.util.Spliterator
import java.util.function.Consumer
import kotlin.reflect.KClass

interface ComponentManager : Iterable<AnyComponent> {
    // Iterators

    fun getComponents(): List<AnyComponent>

    override fun iterator(): Iterator<AnyComponent> = getComponents().iterator()

    override fun spliterator(): Spliterator<AnyComponent> = getComponents().spliterator()

    fun namedIterator(): Iterator<Pair<Name, AnyComponent>>

    val names: List<String>
        get() = namedIterator().asSequence().map { it.first.full }.toList()

    // Operations

    fun <T : AnyComponent> addComponent(
        component: T,
        name: String,
        enable: Boolean = false,
        putState: Boolean = true
    ): T

    fun <T : AnyComponent> replaceComponent(name: String, factory: () -> T): T? {
        val old = removeComponent(name)
        return if (old != null) {
            addComponent(factory(), name)
        } else null
    }

    fun getComponentOrAdd(
        name: String,
        enable: Boolean = false,
        factory: () -> AnyComponent,
    ): AnyComponent {
        getComponent(name)?.let { return it }
        return addComponent(factory(), name = name, enable = enable)
    }

    fun <T : AnyComponent> getComponentOrAdd(
        kClass: KClass<T>,
        setName: String,
        factory: () -> T,
        enable: Boolean
    ): AnyComponent {
        getComponentOfClass(kClass)?.let { return it }
        return addComponent(factory(), name = setName, enable = enable)
    }

    fun removeComponent(component: AnyComponent): AnyComponent?

    fun removeComponent(name: String): AnyComponent?

    fun removeAll() = forEach { removeComponent(it) }

    // Getters

    fun getComponent(name: String): AnyComponent?

    fun <T : Any> getComponentOfClass(clazz: KClass<T>): T?

    fun <T : Any> getComponentsOfClass(clazz: KClass<T>): List<T>

    fun requireComponentByName(name: String): AnyComponent {
        return getComponent(name) ?: throw ComponentNotFoundException("Компонент $name не найден")
    }

    // State manager

    fun enableAll() {
        forEach { if (it is Activateable) it.enable() }
    }

    override fun forEach(action: Consumer<in AnyComponent>?) {
        iterator().forEach { action?.accept(it) }
    }
}