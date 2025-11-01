package org.qbrp.main.core.components.extensions

import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.engine.items.components.ExactTick
import org.qbrp.main.engine.items.components.StateTick
import kotlin.reflect.KClass

// DI

object GlobalComponentRegistryDependencyContainer : KoinComponent {
    val registry: ComponentRegistry = GlobalContext.get().get()
}

private val globalComponentRegistry get() = GlobalComponentRegistryDependencyContainer.registry

// Tick

fun ComponentManager.tick() {
    forEach { if (it is StateTick) it.tickState() }
}

fun ComponentManager.tickExact() {
    forEach { if (it is ExactTick) it.tickStateExact() }
}

// Copy

fun ComponentManager.copy(
    destination: ComponentManager,
    predicate: (String, AnyComponent) -> Boolean = { _, _ -> true }
): ComponentManager {
    namedIterator().forEach { (name, component) ->
        if (predicate(name.full, component)) {
            destination.addComponent(component, name = name.full, enable = false)
        }
    }
    return destination
}

// Add

fun <T : AnyComponent> ComponentManager.add(
    component: T,
    name: String = globalComponentRegistry.getComponentName(component),
    enable: Boolean = false,
    putState: Boolean = true
): T {
    return addComponent(component, name, enable, putState)
}

// Get or add

fun <T : AnyComponent> ComponentManager.getOrAdd(
    kClass: KClass<T>,
    enable: Boolean = false,
    name: String = globalComponentRegistry.getComponentName(kClass),
    factory: () -> T
): T {
    return getComponentOrAdd(name, enable, { factory() }) as T
}

inline fun <reified T : AnyComponent> ComponentManager.getOrAdd(
    enable: Boolean = false,
    noinline factory: () -> T
): T {
    return getOrAdd(T::class, enable = enable, factory = factory)
}


inline fun <reified T : AnyComponent> ComponentManager.getOrAdd(
    enable: Boolean = false,
    setName: String,
    noinline factory: () -> T
): T {
    return getOrAdd(T::class, enable, setName, factory)
}

// Add or Modify

inline fun <reified T : AnyComponent> ComponentManager.addOrModify(
    noinline factory: () -> T,
    noinline modify: T.() -> Unit,
    enable: Boolean = false,
): T {
    val component = get<T>()
    return if (component != null) {
        component.modify()
        component
    } else {
        add(factory(), enable = enable)
    }
}

// Add or Replace

inline fun <reified T : AnyComponent> ComponentManager.replaceOrAdd(
    enable: Boolean = false,
    noinline factory: () -> T,
): T {
    return replace(factory) ?: add(factory(), enable = enable)
}

// Remove

fun ComponentManager.remove(
    kClass: KClass<out AnyComponent>,
    registry: ComponentRegistry = globalComponentRegistry
): AnyComponent? {
    return removeComponent(registry.getComponentName(kClass))
}

inline fun <reified T : AnyComponent> ComponentManager.remove(): T? = remove(T::class) as? T?

inline fun <reified T : AnyComponent> ComponentManager.removeIf() {
    getComponentsOfClass(T::class).forEach { component -> removeComponent(component) }
}

// Get

inline fun <reified T : Any> ComponentManager.get(): T? {
    return getComponentOfClass(T::class)
}

inline fun <reified T : Any> ComponentManager.getAs(name: String): T? {
    return getComponent(name) as? T
}

// Require

inline fun <reified T : Any> ComponentManager.require(): T {
    return get<T>() ?: throw ComponentNotFoundException("Компонент ${T::class.simpleName} не найден")
}

// Has

fun ComponentManager.has(kClass: KClass<out Any>): Boolean {
    return getComponentsOfClass(kClass).isNotEmpty()
}

inline fun <reified T : Any> ComponentManager.has(): Boolean {
    return has(T::class)
}

// Replace

fun <T : AnyComponent> ComponentManager.replace(
    klass: KClass<out T>,
    name: String = globalComponentRegistry.getComponentName(klass),
    factory: () -> T
): T? {
    return replaceComponent(name, factory)
}

fun <T : AnyComponent> ComponentManager.replaceIfInstance(
    klass: KClass<out T>,
    setName: String = globalComponentRegistry.getComponentName(klass),
    enable: Boolean = true,
    factory: (T) -> T
): List<T> {
    val removedComponents = mutableListOf<T>()
    for (component in getComponentsOfClass(klass)) {
        val deleted = removeComponent(component) as? T
        if (deleted != null) {
            addComponent(factory(deleted), name = setName, enable = enable)
            removedComponents += component
        }
    }
    return removedComponents
}


inline fun <reified T : AnyComponent> ComponentManager.replace(
    noinline factory: () -> T
): T? {
    return replace(T::class, factory = factory)
}

// All

inline fun <reified T : Any> ComponentManager.all(): List<T> {
    return getComponentsOfClass(T::class)
}

//
// With context
//
//
//// Add
//
//context(ctx: ComponentRegistry)
//fun <T : AnyComponent> ComponentManager.add(
//    component: AnyComponent,
//    enable: Boolean = false,
//    putState: Boolean = true
//) = add(component, ctx, enable, putState) as T
//
//@JvmName("addAndReturnUnit")
//context(ctx: ComponentRegistry)
//fun ComponentManager.add(
//    component: AnyComponent,
//    enable: Boolean = false,
//    putState: Boolean = true
//) = add(component, ctx, enable, putState)
//
//context(ctx: ComponentRegistry)
//fun ComponentManager.remove(kClass: KClass<out AnyComponent>): AnyComponent? =
//    remove(kClass, ctx)
//
//context(ctx: ComponentRegistry)
//inline fun <reified T : AnyComponent> ComponentManager.remove(): T? =
//    remove(T::class, ctx) as? T?
//
//context(ctx: ComponentRegistry)
//fun <T: AnyComponent> ComponentManager.get(klass: KClass<out T>): T? =
//    get(klass, ctx)
//
//context(ctx: ComponentRegistry)
//fun <T: AnyComponent> ComponentManager.getOrAdd(
//    kclass: KClass<out T>,
//    enable: Boolean = false,
//    factory: () -> T
//): T = getOrAdd(kclass, enable, ctx, factory)
//
//context(ctx: ComponentRegistry)
//inline fun <reified T : AnyComponent> ComponentManager.getOrAdd(
//    enable: Boolean = false,
//    noinline factory: () -> T
//): T = getOrAdd(T::class, enable, ctx, factory)
//
//context(ctx: ComponentRegistry)
//fun <T : AnyComponent> ComponentManager.replace(
//    klass: KClass<out T>,
//    factory: () -> T
//): T? = replace(klass, ctx, factory)
//
//context(ctx: ComponentRegistry)
//inline fun <reified T: AnyComponent> ComponentManager.replace(
//    noinline factory: () -> T
//): T? = replace(T::class, factory)
