package org.qbrp.main.engine.containers.v2.serialization

import kotlinx.coroutines.runInterruptible
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.Capacity.FixedMass
import org.qbrp.main.engine.containers.v2.components.Capacity.Infinity
import org.qbrp.main.engine.containers.v2.components.Capacity.Physical
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.asEntryReference
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.model.agent.Agent

@Serializable
data class ContainerDto(
    val items: List<String> = listOf(),
    val capacity: CapacityDto = CapacityDto.Infinity,
    val name: String? = null,
    val containerId: ContainerId = ContainerId.random()
) : DTO

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed class CapacityDto {
    @Serializable @SerialName("physical")
    data class Physical(val factor: Float = 1.0f) : CapacityDto()
    @Serializable @SerialName("fixed")
    data class Fixed(val mass: Float) : CapacityDto()
    @Serializable @SerialName("infinity")
    object Infinity : CapacityDto()
}

fun containerToDto(id: ContainerId, service: ContainerService): ContainerDto {
    val items = service.getItems(id)
    val name = service.getName(id)
    val capacity = service.getCapacity(id).let {
        when (it) {
            is Capacity.FixedMass -> CapacityDto.Fixed(it.mass)
            is Capacity.Physical -> CapacityDto.Physical(it.factor)
            is Capacity.Infinity -> CapacityDto.Infinity
        }
    }
    return ContainerDto(items.map { it.id }, capacity, name.value, id)
}

interface ContainerDisplay {
    val containerDisplayName: ContainerDisplayName
}

class ContainerAnchorMapper(private val service: ContainerService) : ContextualDtoMapper<ContainerDto, Container, ComponentMapContext>, DtoSerializable {
    override val serializerRegistryId: SerializerRegistryId
        get() = SerializerRegistryId.ofComponent("Container")

    override fun toDto(obj: Container, ctx: ComponentMapContext): ContainerDto {
        return containerToDto(obj.id, service)
    }

    override fun fromDto(dto: ContainerDto, ctx: ComponentMapContext): Container {
        val state = ctx.state
        val items = dto.items.map { it.asEntryReference() }
        val agent = state.get<Agent>()
        val capacity = fromCapacityDto(dto.capacity, agent)
        val id = dto.containerId
        val name = dto.name?.let { ContainerDisplayName(it) } ?: state.get<ContainerDisplay>()?.containerDisplayName ?: ContainerDisplayName("Инвентарь")
        service.newContainer(
            name,
            capacity,
            items,
            id
        )
        return Container(id)
    }

    fun fromCapacityDto(dto: CapacityDto, agent: Agent? = null): Capacity {
        return when (dto) {
            is CapacityDto.Physical -> Physical(agent!!, dto.factor)
            is CapacityDto.Fixed -> FixedMass(dto.mass)
            is CapacityDto.Infinity -> Infinity
        }
    }
}