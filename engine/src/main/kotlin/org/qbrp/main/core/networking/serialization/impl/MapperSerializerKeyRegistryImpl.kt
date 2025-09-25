package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry

class MapperSerializerKeyRegistryImpl(private val registries: List<SerializerKeyRegistry>): SerializerKeyRegistry {
    override fun getSerializerKeyNull(obj: Any): SerializerRegistryId? {
        val mapper = registries.firstNotNullOfOrNull { it.getSerializerKey(obj) }
        return mapper
    }
}