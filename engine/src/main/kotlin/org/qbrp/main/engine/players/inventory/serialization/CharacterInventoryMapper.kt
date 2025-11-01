package org.qbrp.main.engine.players.inventory.serialization

import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.serialization.ContainerAnchorMapper
import org.qbrp.main.engine.containers.serialization.ContainerDto
import org.qbrp.main.engine.containers.serialization.containerToDto
import org.qbrp.main.engine.containers.serialization.loadContainer
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.players.characters.CharacterRecord
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.CharacterInventoryContainers
import org.qbrp.main.engine.players.inventory.CharacterContainer

class CharacterInventoryMapper(
    private val containerService: ContainerService,
    private val manipulator: ManipulatorSystem
) : ContextualDtoMapper<CharacterInventoryDto, CharacterInventory, ComponentMapContext>, DtoSerializable {
    override val serializerRegistryId: SerializerRegistryId = SerializerRegistryId.ofComponent("CharacterInventory")

    override fun toDto(
        obj: CharacterInventory,
        ctx: ComponentMapContext
    ): CharacterInventoryDto {
        return CharacterInventoryDto(
            containerToDto(obj.leftHand.id, containerService),
            containerToDto(obj.rightHand.id, containerService),
            containerToDto(obj.equipment, containerService)
        )
    }

    override fun fromDto(
        dto: CharacterInventoryDto,
        ctx: ComponentMapContext
    ): CharacterInventory {
        val profile = ctx.state as CharacterRecord
        val containers = CharacterInventoryContainers(
            loadContainer(dto.equipment, containerService),
            loadContainer(dto.lHand, containerService),
            loadContainer(dto.rHand, containerService)
        )
        return CharacterInventory(profile.ctx, containers, containerService)
    }
}

class CharacterContainerMapper(
    private val containerAnchorMapper: ContainerAnchorMapper
) : ContextualDtoMapper<ContainerDto, CharacterContainer, ComponentMapContext> {
    override fun fromDto(
        dto: ContainerDto,
        ctx: ComponentMapContext
    ): CharacterContainer {
        val profile = ctx.state as CharacterRecord
        return CharacterContainer(profile.ctx, containerAnchorMapper.fromDto(dto, ctx).id)
    }

    override fun toDto(
        obj: CharacterContainer,
        ctx: ComponentMapContext
    ): ContainerDto {
        return containerAnchorMapper.toDto(Container(obj.id), ctx)
    }
}