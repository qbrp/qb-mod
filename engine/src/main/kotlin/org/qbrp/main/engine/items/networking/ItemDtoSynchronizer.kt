package org.qbrp.main.engine.items.networking

import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.impl.IdObjectDtoMappingContext
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.synchronization.DtoPacketSynchronizer
import org.qbrp.main.core.networking.synchronization.DtoSynchronizePacket
import org.qbrp.main.core.networking.synchronization.DtoTask
import org.qbrp.main.engine.items.model.ItemObject

class ItemDtoSynchronizer(private val state: AssociatedComponentState<ItemObject>,
                          mapper: GlobalComponentPacketDtoMapper,
                          packetSender: ServerPacketSender,
                          serializerKeyRegistry: SerializerKeyRegistry,
                          override val dtoChannelName: String
): DtoPacketSynchronizer(state, serializerKeyRegistry, mapper, packetSender) {
    override fun createDtoPacket(dtos: List<DtoTask>): DtoSynchronizePacket<*>? {
        return DtoSynchronizePacket<ItemObject>(state.parent.id, dtos)
    }

    override fun createContext(): IdObjectDtoMappingContext {
        return IdObjectDtoMappingContext(state.parent)
    }
}