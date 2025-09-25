package org.qbrp.main.core.networking.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy

class SerializersRegistryImpl: SerializersRegistry {
    private val serializers: MutableMap<SerializerRegistryId, SerializationStrategy<*>> = mutableMapOf()
    private val deserializers: MutableMap<SerializerRegistryId, DeserializationStrategy<*>> = mutableMapOf()

    override fun <T> getSerializationStrategy(id: SerializerRegistryId): SerializationStrategy<T> {
        @Suppress("UNCHECKED_CAST")
        return (serializers[id] ?: throw NullPointerException("Cериализатор не найден: $id")) as SerializationStrategy<T>
    }

    override fun <T> registerSerializationStrategy(
        id: SerializerRegistryId,
        strategy: SerializationStrategy<T>
    ) {
//        if (serializers.containsKey(id)) { throw IllegalArgumentException("Сериализация уже зарегистрирована: $id") }
        serializers[id] = strategy
    }

    override fun <T> getDeserializationStrategy(id: SerializerRegistryId): DeserializationStrategy<T> {
        @Suppress("UNCHECKED_CAST")
        return (deserializers[id] ?: throw NullPointerException("Десериализатор не найден: $id")) as DeserializationStrategy<T>
    }

    override fun <T> registerDeserializationStrategy(
        id: SerializerRegistryId,
        strategy: DeserializationStrategy<T>
    ) {
//        if (deserializers.containsKey(id)) { throw IllegalArgumentException("Десериализация уже зарегистрирована: $id") }
        deserializers[id] = strategy
    }

    override fun <T> registerSerializer(id: SerializerRegistryId, serializer: KSerializer<T>) {
        serializers[id] = serializer
        deserializers[id] = serializer
    }
}