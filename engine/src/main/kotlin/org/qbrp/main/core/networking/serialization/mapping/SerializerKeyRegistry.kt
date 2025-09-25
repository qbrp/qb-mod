package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.networking.serialization.SerializerRegistryId

interface SerializerKeyRegistry {
    fun getSerializerKeyNull(obj: Any): SerializerRegistryId?
    fun getSerializerKey(obj: Any): SerializerRegistryId {
        return getSerializerKeyNull(obj) ?: error("No serializer registered for object: ${obj::class.simpleName}")
    }
}