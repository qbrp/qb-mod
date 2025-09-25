package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.SerializerRegistryId

data class DtoTask(val registryId: SerializerRegistryId, val data: DTO, val name: String)