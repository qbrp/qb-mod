package org.qbrp.main.core.networking.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface SerializersRegistry {
    fun <T> getSerializationStrategy(id: SerializerRegistryId): SerializationStrategy<T>
    fun <T> registerSerializationStrategy(id: SerializerRegistryId, strategy: SerializationStrategy<T>)

    fun <T> getDeserializationStrategy(id: SerializerRegistryId): DeserializationStrategy<T>
    fun <T> registerDeserializationStrategy(id: SerializerRegistryId, strategy: DeserializationStrategy<T>)

    fun <T> registerSerializer(id: SerializerRegistryId, serializer: KSerializer<T>)

    @OptIn(InternalSerializationApi::class)
    fun <T: Any> registerDeserializationStrategy(id: SerializerRegistryId, dtoClass: KClass<T>) {
        registerDeserializationStrategy(id, dtoClass.serializer())
    }

    @OptIn(InternalSerializationApi::class)
    fun <T: Any> registerSerializationStrategy(id: SerializerRegistryId, dtoClass: KClass<T>) {
        registerSerializationStrategy(id, dtoClass.serializer())
    }

    @OptIn(InternalSerializationApi::class)
    fun <T: Any> registerSerializer(id: SerializerRegistryId, dtoClass: KClass<T>) {
        registerSerializer(id, dtoClass.serializer())
    }
}