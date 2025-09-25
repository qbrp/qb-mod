package org.qbrp.client.deprecated
//
//import org.qbrp.client.engine.inventory.space.SpaceHolder
//import org.qbrp.client.engine.inventory.space.SpaceInformation
//import org.qbrp.client.engine.items.ClientItemObject
//import org.qbrp.client.engine.items.ClientItemStorage
//import org.qbrp.main.core.components.objects.StateObject
//import org.qbrp.main.core.dataformats.StringId
//import org.qbrp.main.core.dataformats.getEntities
//import org.qbrp.main.core.dataformats.toIds
//import org.qbrp.main.core.networking.serialization.SerializerRegistryId
//import org.qbrp.main.core.networking.serialization.impl.ComponentDtoMappingContext
//import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
//import org.qbrp.main.core.networking.serialization.mapping.ctx.ComponentPacketDtoMapper
//import org.qbrp.main.core.networking.serialization.serializerId
//import org.qbrp.main.core.networking.synchronization.DtoSyncComponentListener
//import org.qbrp.main.engine.containers.util.ContainerId
//import org.qbrp.main.engine.inventory.packet.SpacePacketDto
//
//class ClientSpacePacketMapper(
//    private val itemStorage: ClientItemStorage
//): ComponentPacketDtoMapper<SpacePacketDto, SpaceHolder, StateObject<*>>,
//    DtoSyncComponentListener<SpacePacketDto, SpaceHolder>, DtoSerializable {
//    override val serializerRegistryId: SerializerRegistryId = itemStorage::class.serializerId()
//
//    override fun fromDto(
//        dto: SpacePacketDto,
//        ctx: ComponentDtoMappingContext<StateObject<*>>
//    ): SpaceHolder {
//        return SpaceHolder(
//            dto.label,
//            dto.ids.toIds().getEntities<ClientItemObject>(itemStorage).filterNotNull(),
//            SpaceInformation()
//                .apply { info = dto.info; progressBarValue = dto.fulness.toFloat() },
//            ContainerId(dto.containerId),
//            StringId(dto.objectId)
//        )
//    }
//
//    override fun toDto(
//        obj: SpaceHolder,
//        ctx: ComponentDtoMappingContext<StateObject<*>>
//    ): SpacePacketDto {
//        throw NotImplementedError()
//    }
//
//    override fun componentExists(
//        dto: SpacePacketDto,
//        component: SpaceHolder
//    ) {
//        component.textInfo.updateValues(dto)
//    }
//}