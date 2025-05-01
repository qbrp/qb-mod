package org.qbrp.core.game.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.koin.core.context.GlobalContext
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.model.components.Activateable
import org.qbrp.core.game.model.components.Behaviour
import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.model.components.Loadable
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.game.model.components.exception.ComponentNotFoundException
import org.qbrp.core.game.model.components.json.ComponentJsonField
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.system.utils.log.Loggers

open class State @JsonCreator constructor(
    @JsonProperty("components")
    val jsonComponents: MutableList<ComponentJsonField> = mutableListOf()
) {
    companion object {
        private val REGISTRY = GlobalContext.get().get<ComponentsRegistry>()
        private val LOGGER = Loggers.get("game", "components")
    }
    var behaviours = mutableListOf<Behaviour>()
    open lateinit var obj: BaseObject
    open var tickables = mutableListOf<Tick<*>>()
    @JsonIgnore val components: MutableMap<String, Component> = mutableMapOf()

    fun putObject(obj: BaseObject) {
        this.obj = obj
    }

    @JsonIgnore
    fun <T: BaseObject> getObject(): T {
        return this.obj as T
    }

    fun loadJson(list: List<ComponentJsonField>) {
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
        enable: Boolean = true
    ) {
        // Проверяем, нет ли уже компонента того же конкретного класса
        val exists = getComponentByName(name) != null
        if (!exists) {
            println("Добавлен компонент ${name}")
            addComponent(component, name, enable)
        }
    }


    fun addComponent(component: Component, name: String = REGISTRY.getComponentName(component), enable: Boolean = true) {
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