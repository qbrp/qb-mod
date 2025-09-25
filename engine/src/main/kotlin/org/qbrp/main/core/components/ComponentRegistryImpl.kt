package org.qbrp.main.core.components

import org.qbrp.main.core.components.exception.ComponentCollisionException
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.SerializersRegistryImpl
import kotlin.reflect.KClass

typealias DtoComponentConstructor = (ComponentManager, DTO) -> AnyComponent
typealias ComponentConstructor = (ComponentManager) -> AnyComponent
typealias SynchronizationStrategy = (AnyComponent, DTO) -> Unit

class ComponentRegistryImpl(private val mapperRegistry: ComponentDtoMapper, private val serializersRegistry: SerializersRegistry): ComponentRegistry {
    private val componentNameMap: MutableMap<String, KClass<out AnyComponent>> = mutableMapOf()
    private val classToNameMap: MutableMap<KClass<out AnyComponent>, String> = mutableMapOf()
    private val entries: MutableMap<String, ComponentRegistry.Entry> = mutableMapOf()

    override fun getEntry(id: String): ComponentRegistry.Entry {
        return entries[id] ?: throw ComponentNotFoundException(id)
    }

    override fun getEntry(kclass: KClass<out AnyComponent>): ComponentRegistry.Entry {
        return getEntry(getComponentName(kclass))
    }

    override fun register(method: (ComponentRegistry.ComponentEntryBuilder).() -> Unit) {
        val entry = ComponentRegistry.ComponentEntryBuilder(mapperRegistry, serializersRegistry)
            .apply { method(this) }
            .build()
        componentNameMap.putIfAbsent(entry.id, entry.kClass).let {
            if (it != null) throw ComponentCollisionException("Компонент ${entry.kClass.simpleName} уже зарегистрирован в реестре!")
        }
        entries[entry.id] = entry
        classToNameMap[entry.kClass] = entry.id
    }

    override fun invokeFactory(name: String, operator: ComponentManager, data: DTO): AnyComponent {
        val entry = entries[name]  ?: throw ComponentNotFoundException("Компонент $name не найден в реестре")
        val constructor = entry.dtoConstructor
        return constructor!!.invoke(operator, data)
    }

    override fun getComponentName(component: AnyComponent): String {
        return classToNameMap[component::class]
            ?: throw ComponentNotFoundException("Компонент ${component::class.simpleName} не зарегистрирован")
    }
    override fun getComponentName(klass: KClass<out AnyComponent>): String {
        return classToNameMap[klass]
            ?: throw ComponentNotFoundException("Компонент ${klass.simpleName} не зарегистрирован")
    }
}