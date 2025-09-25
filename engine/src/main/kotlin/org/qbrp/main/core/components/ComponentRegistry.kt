package org.qbrp.main.core.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualFromDtoStrategy
import kotlin.reflect.KClass

interface ComponentRegistry {
    fun getEntry(id: String): Entry
    fun getEntry(kclass: KClass<out AnyComponent>): Entry
    fun register(method: (ComponentEntryBuilder).() -> Unit)
    fun invokeFactory(name: String, operator: ComponentManager, data: DTO): AnyComponent
    fun getComponentName(component: AnyComponent): String
    fun getComponentName(klass: KClass<out AnyComponent>): String

    data class Entry(val id: String,
                     val kClass: KClass<out AnyComponent>,
                     val dto: KClass<out DTO>?,
                     val serializer: SerializationStrategy<DTO>?,
                     val deserializer: DeserializationStrategy<DTO>?,
                     val dtoConstructor: DtoComponentConstructor?,
                     val constructor: ComponentConstructor?) {
        fun requireConstructor(): ComponentConstructor = constructor!!
        override fun toString(): String {
            return id
        }
    }

    class ComponentEntryBuilder(val componentMapperRegistry: ComponentDtoMapper, val serializerRegistry: SerializersRegistry) {
        var id: String? = null
        var clazz: KClass<out AnyComponent>? = null
        var dto: KClass<out DTO>? = null
        private var _serializer: SerializationStrategy<DTO>? = null
        private var _deserializer: DeserializationStrategy<DTO>? = null
        private var _dtoConstructor: DtoComponentConstructor? = null
        private var _constructor: ComponentConstructor? = null

        @OptIn(InternalSerializationApi::class)
        fun serializer(clazz: KClass<out DTO>) {
            _serializer = clazz.serializer() as SerializationStrategy<DTO>?
            val key = SerializerRegistryId.ofComponent(id!!)
            componentMapperRegistry.registerSerializerKey(this.clazz!!, key)
            serializerRegistry.registerSerializationStrategy(key, _serializer!!)
        }

        @OptIn(InternalSerializationApi::class)
        fun deserializer(clazz: KClass<DTO>) {
            _deserializer = clazz.serializer()
            val key = SerializerRegistryId.ofComponent(id!!)
            componentMapperRegistry.registerSerializerKey(this.clazz!!, key)
            serializerRegistry.registerDeserializationStrategy(key, _deserializer!!)
        }

        inline fun <reified D: DataComponent> data(includeSerializers: Boolean = true) {
            componentClass(D::class)
            serializeConstructor<D, ComponentManager, D>(includeSerializers) { _, dto -> dto }
        }

        fun componentClass(clazz: KClass<out AnyComponent>) {
            this.clazz = clazz
            this.id = clazz.simpleName
        }

        fun <D: DTO, O: ComponentManager> dtoConstructor(factory: (O, D) -> AnyComponent) {
            this._dtoConstructor = factory as DtoComponentConstructor
        }

        fun <O: ComponentManager> standartConstructor(factory: (O) -> AnyComponent) {
            this._constructor = factory as ComponentConstructor
        }

        inline fun <reified D: DTO, O: ComponentManager, reified A: AnyComponent> serializeConstructor(includeSerializers: Boolean = true,
                                                                                                       noinline factory: (O, D) -> A) {
            dtoConstructor<D, O>(factory)
            dto = D::class
            if (includeSerializers) {
                serializer(D::class)
                deserializer(D::class as KClass<DTO>)
            }
            componentMapperRegistry.registerFromDtoStrategy(
                ContextualFromDtoStrategy<D, A, ComponentMapContext> { dto, ctx ->
                    factory.invoke(ctx.state as O, dto)
                }, A::class, D::class
            )
        }

        fun build(): Entry {
            return Entry(id!!,
                clazz!!, dto,
                _serializer,
                _deserializer,
                _dtoConstructor,
                _constructor)
        }
    }
}