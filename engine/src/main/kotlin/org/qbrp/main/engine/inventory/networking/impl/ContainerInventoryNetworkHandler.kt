package org.qbrp.main.engine.inventory.networking.impl

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.inventory.InventoriesModule
import org.qbrp.main.engine.inventory.networking.ContainerSyncListener
import org.qbrp.main.engine.inventory.networking.InventoryPacketSender
import org.qbrp.main.engine.inventory.networking.InventorySynchronizer
import org.qbrp.main.engine.inventory.packet.ContainerSyncRequest
import org.qbrp.main.engine.inventory.packet.InventoryClosePacket
import org.qbrp.main.engine.inventory.packet.InventoryGroupOpenPacket
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.engine
import org.qbrp.main.engine.players.model.networking.DisconnectReason

class ContainerInventoryNetworkHandler(private val activator: PacketListenerActivator<ServerNetworkContext>,
                                       private val packetSender: ServerPacketSender,
                                       private val synchronizer: InventorySynchronizer,
                                       val service: ContainerService
): ContainerSyncListener,
    InventoryPacketSender {
    fun run() {
        activator.run(InventoriesModule.Companion.SYNC, this, { buf, ctx ->
            ContainerSyncRequest(
                buf,
                ctx.player.engine
            )
        })
    }

    override fun onPacket(p: Packet<PacketListener>) {}

    context(ctx: MinecraftContext)
    override fun onPacket(p: Packet<PacketListener>, player: ServerPlayerObject) {
        try {
            p.apply(this)
        } catch (e: Exception) {
            e.printStackTrace()
            player.disconnect(DisconnectReason("Получены неверные данные: ${e.message}"))
        }
    }

    override fun onSyncRequest(
        player: ServerPlayerObject,
        containerId: ContainerId,
    ) {
        synchronizer.sync(containerId, player)
    }

    override fun sendInventoryGroupPacket(player: ServerPlayerObject, group: InventoryGroup) {
        packetSender.sendImmediately(InventoryGroupOpenPacket(group), InventoriesModule.OPEN, player)
    }

    override fun sendInventoryClosePacket(player: ServerPlayerObject, id: ContainerId) {
        packetSender.sendImmediately(InventoryClosePacket(id), InventoriesModule.CLOSE, player)
    }
}