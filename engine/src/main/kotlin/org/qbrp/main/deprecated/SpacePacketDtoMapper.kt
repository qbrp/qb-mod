package org.qbrp.main.deprecated

//import org.qbrp.main.engine.containers.v1.space.SpaceComponent
//
//class SpacePacketDtoMapper<C: SpaceComponent<*>>() : ComponentPacketDtoMapper<SpacePacketDto, C, StateObject<*>>, DtoSerializable {
//    override val serializerRegistryId: SerializerRegistryId = SpacePacketDto::class.serializerId()
//
//    override fun toDto(
//        obj: C,
//        ctx: ComponentDtoMappingContext<StateObject<*>>
//    ): SpacePacketDto {
//        return SpacePacketDto(
//            obj.container.contents.solids.map { it.id },
//            obj.label.value,
//            ctx.componentManager.id,
//            obj.cId.id,
//            obj.container.fullness,
//            obj.getDescription(),
//        )
//    }
//
//    override fun fromDto(
//        dto: SpacePacketDto,
//        ctx: ComponentDtoMappingContext<StateObject<*>>
//    ): C {
//        throw NotImplementedError()
//    }
//}