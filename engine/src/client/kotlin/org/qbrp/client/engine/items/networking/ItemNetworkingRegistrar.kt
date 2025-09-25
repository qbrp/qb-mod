package org.qbrp.client.engine.items.networking

import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.core.resources.ModelRepository
import org.qbrp.client.core.networking.utils.DtoSyncResolver
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.engine.items.networking.ItemSyncPacket
import org.qbrp.main.engine.items.networking.ItemSynchronizer
import org.qbrp.main.mechanics.items.display.ItemDisplayPacket
import org.qbrp.main.mechanics.items.display.ItemDtoPacket
import org.qbrp.main.mechanics.items.display.components.ItemDisplay

class ItemNetworkingRegistrar(private val activator: PacketListenerActivator<ClientContext>,
                              private val storage: ClientItemStorage,
                              private val mapper: GlobalComponentPacketDtoMapper,
                              private val modelRepository: ModelRepository) {
    private val baseResolver = ItemSyncResolver(storage)

    fun run() {
        activator.run(ItemSynchronizer.CHANNEL_NAME, baseResolver) { buf, ctx -> ItemSyncPacket(buf) }
        activator.run(ItemDisplay.CHANNEL, ItemDisplayResolver(storage, modelRepository)) { buf, ctx -> ItemDisplayPacket(buf) }
        activator.run(
            ItemSynchronizer.CHANNEL_NAME_DTO,
            DtoSyncResolver(storage, mapper),
            { buf, ctx -> ItemDtoPacket(buf) })
    }
}