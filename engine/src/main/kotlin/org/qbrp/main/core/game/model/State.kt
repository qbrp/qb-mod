package org.qbrp.main.core.game.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.context.GlobalContext
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
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent
import org.qbrp.main.engine.synchronization.`interface`.components.InternalMessageBroadcaster

@Serializable(with = StateSerializer::class)
open class State constructor(val jsonComponents: Collection<ComponentJsonField> = mutableListOf()
): InternalMessageBroadcaster {
    companion object {
        private val REGISTRY = GlobalContext.get().get<ComponentsRegistry>()
        private val LOGGER = LoggerUtil.get("game", "components")
    }
    var behaviours = mutableListOf<Behaviour>()
    open lateinit var obj: BaseObject
    open var tickables = mutableListOf<Tick<*>>()
    @Transient val componentsMap: MutableMap<String, Component> = mutableMapOf()

    fun <T> tick(ctx: T) {
        @Suppress("UNCHECKED_CAST")
        (tickables as MutableList<Tick<T>>).forEach { it.tick(ctx) }
    }

    //java.util.ConcurrentModificationException: null
    fun copy(): State {
        synchronized(this) {
            val newState = State()
            componentsMap.forEach { (k, v) ->
                newState.addComponent(v, k)
            }
            newState.putObject(obj)
            return newState
        }
    }

    override fun broadcastMessage(id: String, content: ClusterViewer) {
        behaviours.forEach {
            it.onMessage(id, content)
        }
    }

    fun putObjectAndEnableBehaviours(obj: BaseObject) {
        putObject(obj)
        val snapshot = behaviours.toList() // Копия, чтобы избежать ConcurrentModificationException
        snapshot.forEach {
            it.enable()
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
        behaviours.clear(); behaviours += componentsMap.values.filterIsInstance<Behaviour>()
        tickables .clear(); tickables  += componentsMap.values.filterIsInstance<Tick<*>>()
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

    inline fun <reified T: Component> getComponentsIsInstance(): List<T> {
        return componentsMap.values.filterIsInstance<T>()
    }

    fun getComponents(): List<Component> {
        return componentsMap.values.toList()
    }

    fun addComponent(component: Component, name: String = REGISTRY.getComponentName(component), enable: Boolean = false) {
        componentsMap[name] = component
            .apply {
                putState(this@State)
                if (this is Loadable) this.load()
                if (this is Activateable && enable) this.enable()
            }
        updateCaches()
    }

    fun removeComponent(component: Component, name: String = REGISTRY.getComponentName(component)) {
        componentsMap[name]?.apply {
            if (this is Activateable) this.disable()
            if (this is Loadable) this.unload()
        }
        componentsMap.remove(name)
        updateCaches()
    }

    fun removeAllComponents() {
        componentsMap.keys.toList().forEach { name ->
            componentsMap[name]?.let { removeComponent(it, name) }
        }
    }

    inline fun <reified T : Component> getComponentOrAdd(factory: () -> T): T {
        componentsMap.values.filterIsInstance<T>().firstOrNull()?.let {
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
        val old = componentsMap.values.filterIsInstance<T>().firstOrNull()
        if (old != null) removeComponent(old)
        val newComp = factory()
        addComponent(newComp)
        return newComp
    }


    inline fun <reified T> getComponent(): T? {
        return componentsMap.values.find { it is T } as? T
    }

    inline fun <reified T> getComponentOrThrow(): T {
        return getComponent<T>() ?: throw ComponentNotFoundException("Компонент ${T::class.qualifiedName} не найден")
    }

    fun getComponentByNameOrThrow(name: String): Component {
        return componentsMap[name] ?: throw ComponentNotFoundException("Компонент $name не найден")
    }

    fun getComponentByName(name: String): Component? {
        return componentsMap[name]
    }
}