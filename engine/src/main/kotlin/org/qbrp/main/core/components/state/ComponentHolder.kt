package org.qbrp.main.core.components.state

import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentInstantiationEvent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.Loadable
import org.qbrp.main.core.components.Name
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.utils.log.LoggerUtil
import java.util.concurrent.ConcurrentHashMap

open class ComponentHolder(): ComponentManager {
    companion object {
        protected val LOGGER = LoggerUtil.get("game", "components")
    }

    val componentsMap: ConcurrentHashMap<String, AnyComponent> = ConcurrentHashMap()

    override fun getComponents(): List<AnyComponent> {
        return componentsMap.values.toList()
    }

    override fun namedIterator(): Iterator<Pair<Name, AnyComponent>> {
        return componentsMap.map { (key, value) -> Pair(Name(key), value) }.iterator()
    }


    override fun <T : AnyComponent> addComponent(component: T, name: String, enable: Boolean, putState: Boolean): T {
        val initialized = component.apply {
            if (this is Loadable) this.load()
            if (this is Activateable && enable) this.enable()
        }
        componentsMap[name] = initialized
        ComponentInstantiationEvent.EVENT.invoker().onInstantiate(this, component)
        return initialized
    }


    override fun removeComponent(c: AnyComponent): AnyComponent? {
        val entry = componentsMap.entries.find { it.value == c }
        return entry?.let {
            componentsMap.remove(it.key)?.apply {
                if (this is Activateable) this.disable()
                if (this is Loadable) this.unload()
            }
        }
    }
    override fun getComponent(name: String): AnyComponent? {
        return componentsMap[name]
    }
}