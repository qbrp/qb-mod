package org.qbrp.main.core.components.extensions

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.engine.items.components.ExactTick
import org.qbrp.main.engine.items.components.StateTick
import kotlin.reflect.KClass

fun ComponentManager.tick() {
    forEach { if (it is StateTick) it.tickState() }
}

fun ComponentManager.tickExact() {
    forEach { if (it is ExactTick) it.tickStateExact() }
}


@Suppress("UNCHECKED_CAST")
fun StateObject<out AssociatedComponentState<*>>.initParent() {
    (state as AssociatedComponentState<Any>).parent = this as StateObject<AssociatedComponentState<Any>>
}

fun ComponentManager.copy(newState: ComponentManager, predicate: (String, AnyComponent) -> Boolean = { _, _ -> true }): ComponentManager {
    namedIterator().forEach { (name, component) ->
        if (predicate(name.full, component)) { newState.addComponent(component, name = name.full, enable = false) }
    }
    return newState
}

fun <T: AnyComponent> ComponentManager.add(component: T,
                         registry: ComponentRegistry = defaultRegistry(),
                         enable: Boolean = false,
                         putState: Boolean = true): T {
    return addComponent(component, registry.getComponentName(component::class), enable, putState) as T
}

fun ComponentManager.remove(kClass: KClass<out AnyComponent>,
                            registry: ComponentRegistry = defaultRegistry()): AnyComponent? {
    return removeComponent(registry.getComponentName(kClass))
}

inline fun <reified T : AnyComponent> ComponentManager.remove(): T? = remove(T::class) as? T?

fun <T: AnyComponent> ComponentManager.get(klass: KClass<out T>, registry: ComponentRegistry = defaultRegistry()): T? {
    return getComponentAs(registry.getComponentName(klass))
}

inline fun <reified T> ComponentManager.get(): T? = getComponents().filterIsInstance<T>().firstOrNull()

inline fun <reified T> ComponentManager.require(): T = get() ?: throw ComponentNotFoundException("Компонент ${T::class.simpleName} не найден")

fun <T: AnyComponent> ComponentManager.getOrAdd(name: String, enable: Boolean = false, factory: () -> T): T {
    return getComponentOrAdd(name, { factory() }, enable)
}

fun <T: AnyComponent> ComponentManager.getOrAdd(kclass: KClass<out T>,
                                                enable: Boolean = false,
                                                registry: ComponentRegistry = defaultRegistry(),
                                                factory: () -> T): T {
    return getOrAdd(registry.getComponentName(kclass), enable, factory)
}

fun <T: AnyComponent> ComponentManager.getOrAdd(kclass: KClass<out T>,
                                                enable: Boolean = false,
                                                factory: () -> T): T {
    return getOrAdd(kclass, enable, defaultRegistry(), factory)
}

inline fun <reified T : AnyComponent> ComponentManager.getOrAdd(enable: Boolean = false, noinline factory: () -> T): T =
    getOrAdd(T::class, enable, factory)

inline fun <reified T> ComponentManager.has(): Boolean = getComponents().filterIsInstance<T>().isNotEmpty()

fun ComponentManager.has(kClass: KClass<out AnyComponent>): Boolean = getComponents().any { it::class == kClass }

fun <T : AnyComponent> ComponentManager.replace(name: String, factory: () -> T): T? {
    val old = removeComponent(name)
    return if (old != null) {
        addComponent(factory(), name) as T
    } else null
}

fun <T : AnyComponent> ComponentManager.replace(klass: KClass<out T>,
                                                registry: ComponentRegistry = defaultRegistry(),
                                                factory: () -> T): T? {
    return replaceComponent(registry.getComponentName(klass), factory)
}

fun <T : AnyComponent> ComponentManager.replace(klass: KClass<out T>,
                                                factory: () -> T): T? {
    return replace(klass, defaultRegistry(), factory)
}


inline fun <reified T: AnyComponent> ComponentManager.replace(noinline factory: () -> T): T? {
    return replace(T::class, factory)
}

private fun defaultRegistry(): ComponentRegistry = GlobalContext.get().get()

context(ctx: ComponentRegistry)
fun <T: AnyComponent> ComponentManager.add(
    component: AnyComponent,
    enable: Boolean = false,
    putState: Boolean = true
) = add(component, ctx, enable, putState) as T

@JvmName("addAndReturnUnit")
context(ctx: ComponentRegistry)
fun ComponentManager.add(
    component: AnyComponent,
    enable: Boolean = false,
    putState: Boolean = true
) = add(component, ctx, enable, putState)

context(ctx: ComponentRegistry)
fun ComponentManager.remove(kClass: KClass<out AnyComponent>): AnyComponent? =
    remove(kClass, ctx)

context(ctx: ComponentRegistry)
inline fun <reified T : AnyComponent> ComponentManager.remove(): T? =
    remove(T::class, ctx) as? T?

context(ctx: ComponentRegistry)
fun <T: AnyComponent> ComponentManager.get(klass: KClass<out T>): T? =
    get(klass, ctx)

context(ctx: ComponentRegistry)
fun <T: AnyComponent> ComponentManager.getOrAdd(
    kclass: KClass<out T>,
    enable: Boolean = false,
    factory: () -> T
): T = getOrAdd(kclass, enable, ctx, factory)

context(ctx: ComponentRegistry)
inline fun <reified T : AnyComponent> ComponentManager.getOrAdd(
    enable: Boolean = false,
    noinline factory: () -> T
): T = getOrAdd(T::class, enable, ctx, factory)

context(ctx: ComponentRegistry)
fun <T : AnyComponent> ComponentManager.replace(
    klass: KClass<out T>,
    factory: () -> T
): T? = replace(klass, ctx, factory)

context(ctx: ComponentRegistry)
inline fun <reified T: AnyComponent> ComponentManager.replace(
    noinline factory: () -> T
): T? = replace(T::class, factory)
