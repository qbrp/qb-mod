package org.qbrp.client.core.networking.utils

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.construct.CtxComponentBuilderOperator
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.networking.serialization.impl.ComponentDtoMappingContext
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.impl.IdObjectDtoMappingContext
import org.qbrp.main.core.networking.synchronization.DtoSynchronizePacket
import org.qbrp.main.core.networking.synchronization.SynchronizePacket

class DtoSyncResolver<O: StateObject<out CtxComponentBuilderOperator<*>>>(storage: Storage<O>, private val mapper: GlobalComponentPacketDtoMapper): StorageGetter<O>(storage) {
    override fun onFound(packet: SynchronizePacket<O>, entity: O) {
        packet as DtoSynchronizePacket<O>
        packet.dto.forEach { dto ->
            val component = entity.getComponent(dto.name)
            if (component == null) {
                val component2 = entity.getComponentOrAdd(
                    dto.name,
                    {
                        mapper.fromDto(
                            dto.data,
                            createContext((entity)) as ComponentDtoMappingContext<ComponentManager>
                        ) as AnyComponent
                    },
                    true
                )
                if (component2 is ClientDtoSyncListener) {
                    component2.onDtoReceived(dto.data)
                }
            } else {
                mapper.componentExists(dto.data, component)
            }
        }
    }

    fun createContext(entity: O): ComponentDtoMappingContext<out ComponentManager> {
        return IdObjectDtoMappingContext(entity)
    }
}
