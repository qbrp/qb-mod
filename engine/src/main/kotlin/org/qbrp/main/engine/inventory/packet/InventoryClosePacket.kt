package org.qbrp.main.engine.inventory.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readGlobalContainerId
import org.qbrp.main.core.networking.writeGlobalContainerId
import org.qbrp.main.core.networking.writeId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.inventory.networking.WidgetsInventoryListener
import org.qbrp.main.engine.containers.util.GlobalContainerId

class InventoryClosePacket(val id: ContainerId): Packet<WidgetsInventoryListener> {
    constructor(buf: PacketByteBuf): this(ContainerId(buf.readString()))

    override fun write(buf: PacketByteBuf) {
        buf.writeId(id)
    }

    override fun apply(listener: WidgetsInventoryListener) {
        listener.onWidgetClose(id)
    }
}