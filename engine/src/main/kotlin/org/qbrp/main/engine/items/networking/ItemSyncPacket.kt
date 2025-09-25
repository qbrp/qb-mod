package org.qbrp.main.engine.items.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.synchronization.SynchronizePacket
import org.qbrp.main.engine.items.model.ItemObject

open class ItemSyncPacket(override val id: String) : SynchronizePacket<ItemObject>() {
    constructor(buf: PacketByteBuf) : this(buf.readString())
    constructor(item: ItemObject) : this(item.id)
}