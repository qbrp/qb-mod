package org.qbrp.main.core.components.state

import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentInstantiationEvent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.Loadable
import org.qbrp.main.core.components.Name
import org.qbrp.main.core.util.log.LoggerUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

open class ComponentHolder : ComponentManager {
    private val componentsMap: ConcurrentHashMap<String, AnyComponent> = ConcurrentHashMap()
    private val componentToNameMap: ConcurrentHashMap<AnyComponent, String> = ConcurrentHashMap()
    private val typeCache: ConcurrentHashMap<KClass<*>, CopyOnWriteArrayList<AnyComponent>> = ConcurrentHashMap()

    override fun getComponents(): List<AnyComponent> = componentsMap.values.toList()

    override fun getComponent(name: String): AnyComponent? = componentsMap[name]

    override fun namedIterator(): Iterator<Pair<Name, AnyComponent>> {
        return componentsMap.map { (key, value) -> Pair(Name(key), value) }.iterator()
    }

    override fun <T : AnyComponent> addComponent(component: T, name: String, enable: Boolean, putState: Boolean): T {
        val initialized = component.apply {
            if (this is Loadable) this.load()
            if (this is Activateable && enable) this.enable()
        }
        if (componentsMap.containsKey(name)) {
            throw IllegalStateException("Component $name already contained in component holder")
        }
        componentsMap[name] = initialized
        componentToNameMap[component] = name

        typeCache.forEach { (cls, set) ->
            if (cls.isInstance(initialized)) {
                set.add(initialized)
            }
        }

        ComponentInstantiationEvent.EVENT.invoker().onInstantiate(this, component)
        return initialized
    }

    override fun removeComponent(component: AnyComponent): AnyComponent? {
        return componentToNameMap[component]?.let { removeComponent(it) }
    }

    override fun removeComponent(name: String): AnyComponent? {
        val removed = componentsMap.remove(name)
        removed?.let {
            if (it is Activateable) it.disable()
            if (it is Loadable) it.unload()

            componentToNameMap.remove(removed)
            typeCache.forEach { (_, set) ->
                set.remove(it)
            }
        }
        return removed
    }

    override fun <T : Any> getComponentOfClass(clazz: KClass<T>): T? {
        return typeCache.computeIfAbsent(clazz) { cls ->
            CopyOnWriteArrayList(componentsMap.values.filter { clazz.isInstance(it) })
        }.firstOrNull() as? T
    }

    override fun <T : Any> getComponentsOfClass(clazz: KClass<T>): List<T> {
        return typeCache.computeIfAbsent(clazz) {
            CopyOnWriteArrayList(componentsMap.values.filter { clazz.isInstance(it) })
        }.mapNotNull { it as? T }
    }

    companion object {
        protected val LOGGER = LoggerUtil.get("game", "components")
    }
}