package org.qbrp.main.engine.containers.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.Capacity
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.entity.asEntryReference
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.components.Capacity.FixedMass
import org.qbrp.main.engine.containers.components.Capacity.Infinity
import org.qbrp.main.engine.containers.components.Capacity.Physical
import org.qbrp.main.engine.chemistry.agent.Agent

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
            is FixedMass -> CapacityDto.Fixed(it.mass)
            is Physical -> CapacityDto.Physical(it.factor)
            is Infinity -> CapacityDto.Infinity
        }
    }
    return ContainerDto(items.map { it.id }, capacity, name.value, id)
}

fun fromCapacityDto(dto: CapacityDto, agent: Agent? = null): Capacity {
    return when (dto) {
        is CapacityDto.Physical -> Capacity.Physical(agent!!, dto.factor)
        is CapacityDto.Fixed -> Capacity.FixedMass(dto.mass)
        is CapacityDto.Infinity -> Capacity.Infinity
    }
}

fun loadContainer(
    dto: ContainerDto,
    service: ContainerService,
    agent: Agent? = null,
    display: ContainerDisplay? = null
) : ContainerId {
    val items = dto.items.map { it.asEntryReference() }
    val capacity = fromCapacityDto(dto.capacity, agent)
    val id = dto.containerId
    val name = dto.name
        ?.let { ContainerDisplayName(it) }
        ?: display?.containerDisplayName
        ?: ContainerDisplayName("Инвентарь")
    return service.newContainer(
        name,
        capacity,
        items,
        id
    )
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
        val id = dto.containerId
        loadContainer(dto, service, state.get(), state.get())
        return Container(id)
    }
}