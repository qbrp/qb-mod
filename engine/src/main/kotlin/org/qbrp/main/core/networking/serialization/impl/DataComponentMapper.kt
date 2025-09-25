package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoMapper
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper

class DataComponentMapper<Component: DataComponent>(
    override val serializerRegistryId: SerializerRegistryId
) : DtoMapper<Component, Component>, ContextualDtoMapper<Component, Component, Any>,  DtoSerializable {
    override fun fromDto(dto: Component): Component {
        return dto
    }

    override fun toDto(obj: Component): Component {
        return obj
    }

    override fun fromDto(dto: Component, ctx: Any): Component {
        return dto
    }

    override fun toDto(dto: Component, ctx: Any): Component {
        return dto
    }
}