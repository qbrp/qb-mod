package org.qbrp.main.engine.inventory.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readGlobalContainerId
import org.qbrp.main.core.networking.writeGlobalContainerId
import org.qbrp.main.core.networking.writeId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.inventory.networking.ContainerSyncListener
import org.qbrp.main.engine.containers.util.GlobalContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

class ContainerSyncRequest(val containerId: ContainerId, val player: ServerPlayerObject? = null): Packet<ContainerSyncListener> {
    constructor(buf: PacketByteBuf, player: ServerPlayerObject) : this(
        ContainerId(buf.readString()), player
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeId(containerId)
    }

    override fun apply(listener: ContainerSyncListener) {
        listener.onSyncRequest(player!!, containerId)
    }
}