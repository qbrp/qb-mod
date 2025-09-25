package org.qbrp.client.engine.items.networking

import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.core.resources.ModelRepository
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.client.engine.items.components.tooltip.ClientItemDisplayComponent
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.networking.synchronization.SynchronizePacket
import org.qbrp.main.mechanics.items.display.ItemDisplayPacket

class ItemDisplayResolver(storage: ClientItemStorage, private val modelRepository: ModelRepository): ItemSyncResolver(storage) {
    override fun onFound(packet: SynchronizePacket<ClientItemObject>, item: ClientItemObject) {
        packet as ItemDisplayPacket
        item
            .getOrAdd { ClientItemDisplayComponent(
                item.state.componentContext,
                modelRepository,
                MinecraftClient.getInstance().bakedModelManager) }
            .apply {
                name = packet.name
                description = packet.description
                model = packet.model
            }
    }
}