package org.qbrp.main.engine.players.inventory.serialization

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.containers.v2.serialization.ContainerAnchorMapper
import org.qbrp.main.engine.containers.v2.serialization.ContainerDto
import org.qbrp.main.engine.containers.v2.serialization.containerToDto
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.characters.entity.CharacterProfile
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.components.CharacterContainer
import org.qbrp.main.engine.players.inventory.hand.Hand

class CharacterInventoryMapper(
    private val containerService: ContainerService,
    private val manipulator: Manipulator
) : ContextualDtoMapper<CharacterInventoryDto, CharacterInventory, ComponentMapContext>, DtoSerializable {
    override val serializerRegistryId: SerializerRegistryId = SerializerRegistryId.ofComponent("CharacterInventory")

    fun ContainerAnchor.toDto(side: Hand.Side): HandDto {
        return HandDto(side, containerService.getItems(id).map { it.id })
    }

    override fun toDto(
        obj: CharacterInventory,
        ctx: ComponentMapContext
    ): CharacterInventoryDto {
        return CharacterInventoryDto(
            obj.leftHand.toDto(Hand.Side.LEFT),
            obj.rightHand.toDto(Hand.Side.RIGHT),
            containerToDto(obj.equipment.container, containerService)
        )
    }

    override fun fromDto(
        dto: CharacterInventoryDto,
        ctx: ComponentMapContext
    ): CharacterInventory {
        val profile = ctx.state as CharacterProfile
        return CharacterInventory(profile.ctx, containerService, ctx.state.require())
    }
}

class CharacterContainerMapper(
    private val containerAnchorMapper: ContainerAnchorMapper
) : ContextualDtoMapper<ContainerDto, CharacterContainer, ComponentMapContext> {
    override fun fromDto(
        dto: ContainerDto,
        ctx: ComponentMapContext
    ): CharacterContainer {
        val profile = ctx.state as CharacterProfile
        return CharacterContainer(profile.ctx, containerAnchorMapper.fromDto(dto, ctx).id)
    }

    override fun toDto(
        obj: CharacterContainer,
        ctx: ComponentMapContext
    ): ContainerDto {
        return containerAnchorMapper.toDto(Container(obj.id), ctx)
    }
}