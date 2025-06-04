package org.qbrp.main.core.game.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.game.model.components.Activateable
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.model.components.Loadable
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.model.components.exception.ComponentNotFoundException
import org.qbrp.main.core.game.serialization.ComponentJsonField
import org.qbrp.main.core.game.serialization.StateSerializer
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.core.game.GameEngine

@Serializable(with = StateSerializer::class)
open class State constructor(val jsonComponents: Collection<ComponentJsonField> = mutableListOf()
) {
    companion object {
        private val REGISTRY = GlobalContext.get().get<ComponentsRegistry>()
        private val LOGGER = LoggerUtil.get("game", "components")
    }
    var behaviours = mutableListOf<Behaviour>()
    open lateinit var obj: BaseObject
    open var tickables = mutableListOf<Tick<*>>()
    @Transient val components: MutableMap<String, Component> = mutableMapOf()

    fun <T> tick(ctx: T) {
        @Suppress("UNCHECKED_CAST")
        (tickables as MutableList<Tick<T>>).forEach { it.tick(ctx) }
    }

    //java.util.ConcurrentModificationException: null
    fun copy(): State {
        synchronized(this) {
            val newState = State()
            components.forEach { (k, v) ->
                newState.addComponent(v, k)
            }
            newState.putObject(obj)
            return newState
        }
    }

    fun putObjectAndEnableBehaviours(obj: BaseObject) {
        putObject(obj)
        behaviours.forEach {
            if (this is Activateable) it.enable()
        }
    }

    fun putObject(obj: BaseObject) {
        this.obj = obj
    }

    fun <T: BaseObject> getObject(): T {
        @Suppress("UNCHECKED_CAST")
        return this.obj as T
    }

    fun loadJson(list: Collection<ComponentJsonField>) {
        list.forEach {
            val name = it.getComponentName()
            try {
                addComponent(it.toComponent(), name)
            } catch (ex: Exception) {
                if (ex is ComponentNotFoundException) {
                    LOGGER.warn("Компонент $name не был найден в реестре и был пропущен")
                } else {
                    throw ex
                }
            }
        }
    }

    init {
        loadJson(jsonComponents)
    }

    private fun updateCaches() {
        behaviours.clear(); behaviours += components.values.filterIsInstance<Behaviour>()
        tickables .clear(); tickables  += components.values.filterIsInstance<Tick<*>>()
    }

    fun addComponentIfNotExist(
        component: Component,
        name: String = REGISTRY.getComponentName(component),
        enable: Boolean = false
    ) {
        // Проверяем, нет ли уже компонента того же конкретного класса
        val exists = getComponentByName(name) != null
        if (!exists) {
            println("Добавлен компонент ${name}")
            addComponent(component, name, enable)
        }
    }


    fun addComponent(component: Component, name: String = REGISTRY.getComponentName(component), enable: Boolean = false) {
        components[name] = component
            .apply {
                putState(this@State)
                if (this is Loadable) this.load()
                if (this is Activateable && enable) this.enable()
            }
        updateCaches()
    }

    fun removeComponent(component: Component, name: String = REGISTRY.getComponentName(component)) {
        components[name]?.apply {
            if (this is Activateable) this.disable()
            if (this is Loadable) this.unload()
        }
        components.remove(name)
        updateCaches()
    }

    fun removeAllComponents() {
        components.keys.toList().forEach { name ->
            components[name]?.let { removeComponent(it, name) }
        }
    }

    inline fun <reified T : Component> getComponentOrAdd(factory: () -> T): T {
        components.values.filterIsInstance<T>().firstOrNull()?.let {
            return it
        }
        val newComp = factory()
        addComponent(newComp)
        return newComp
    }

    inline fun getComponentOrAdd(name: String, factory: () -> Component) {
        getComponentByName(name).let { return }
        val newComp = factory()
        addComponent(newComp)
    }

    inline fun <reified T : Component> replaceComponent(noinline factory: () -> T): T {
        val old = components.values.filterIsInstance<T>().firstOrNull()
        if (old != null) removeComponent(old)
        val newComp = factory()
        addComponent(newComp)
        return newComp
    }


    inline fun <reified T> getComponent(): T? {
        return components.values.find { it is T } as? T
    }

    inline fun <reified T> getComponentOrThrow(): T {
        return getComponent<T>() ?: throw ComponentNotFoundException("Компонент ${T::class.qualifiedName} не найден")
    }

    fun getComponentByNameOrThrow(name: String): Component {
        return components[name] ?: throw ComponentNotFoundException("Компонент $name не найден")
    }

    fun getComponentByName(name: String): Component? {
        return components[name]
    }
}