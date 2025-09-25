package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.networking.serialization.SerializerRegistryId

interface DtoSerializable {
    val serializerRegistryId: SerializerRegistryId
}