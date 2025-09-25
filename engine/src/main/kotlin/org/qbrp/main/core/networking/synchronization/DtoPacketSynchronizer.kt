package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.serialization.impl.ComponentDtoMappingContext
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.engine.players.model.ServerPlayerObject

abstract class DtoPacketSynchronizer(
    private val state: ComponentManager,
    private val serializerKeyRegistry: SerializerKeyRegistry,
    private val mapper: GlobalComponentPacketDtoMapper,
    private val packetSender: ServerPacketSender
) {
    abstract val dtoChannelName: String
    protected open fun createDtoPacket(dtos: List<DtoTask>): DtoSynchronizePacket<*>? = null

    fun syncDtos(players: List<ServerPlayerObject>) {
        val dtoProviders = state.filterIsInstance<DtoSync>()
        for (player in players) {
            val session = player
            val dtos = dtoProviders.map {
                val syncComponent = it.componentToSync(session)
                val name = state.namedIterator().asSequence().find { (name, component) -> component == syncComponent }?.first!!.full
                val dto = mapper.toDto(syncComponent, createContext() as ComponentDtoMappingContext<ComponentManager>)
                val key = serializerKeyRegistry.getSerializerKey(syncComponent)
                DtoTask(
                    key,
                    dto,
                    name
                )
            }
            if (dtos.isNotEmpty()) {
                createDtoPacket(dtos)?.let {
                    packetSender.sendImmediately(it, dtoChannelName, player)
                }
            }
        }
    }

    open fun createContext(): ComponentDtoMappingContext<out ComponentManager> {
        return object : ComponentDtoMappingContext<ComponentManager> {
            override val componentManager: ComponentManager
                get() = state
        }
    }
}