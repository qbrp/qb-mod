package org.qbrp.main.engine.inventory.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readContainerId
import org.qbrp.main.core.networking.readEnum
import org.qbrp.main.core.networking.writeEnum
import org.qbrp.main.core.networking.writeId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.entity.OccupancyView
import org.qbrp.main.engine.inventory.networking.InventoryListener
import org.qbrp.main.engine.inventory.packet.ContainerSyncPacket.Companion.OccupancyType.LIMITED
import org.qbrp.main.engine.inventory.packet.ContainerSyncPacket.Companion.OccupancyType.UNLIMITED

open class ContainerSyncPacket(val containerId: ContainerId,
                               val entries: List<ContainerEntry>,
                               val name: ContainerDisplayName,
                               val occupancy: OccupancyView): Packet<InventoryListener> {
    constructor(buf: PacketByteBuf, storage: Storage<out ContainerEntry>): this(
        buf.readContainerId(),
        buf.readEntries(storage),
        ContainerDisplayName(buf.readString()),
        buf.readOccupancy()
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeId(containerId)
        buf.writeEntries(entries)
        buf.writeString(name.value)
        buf.writeOccupancy(occupancy)
    }

    override fun apply(listener: InventoryListener) {
        listener.onItems(entries, containerId)
        listener.onName(containerId, name)
        listener.onOccupancy(containerId, occupancy)
    }

    fun PacketByteBuf.writeEntries(entries: List<ContainerEntry>) {
        writeVarInt(entries.size)
        entries.forEach { writeString(it.id) }
    }

    companion object {
        fun PacketByteBuf.readEntries(storage: Storage<out ContainerEntry>): List<ContainerEntry> {
            return List(readVarInt()) { storage.getById(readString()) }.filterNotNull()
        }

        fun PacketByteBuf.writeOccupancy(occupancy: OccupancyView) {
            writeEnum(occupancy.type)
            if (occupancy is OccupancyView.Limited) writeDouble(occupancy.fullness)
        }

        fun PacketByteBuf.readOccupancy(): OccupancyView {
            return when(readEnum<OccupancyType>()) {
                OccupancyType.LIMITED -> OccupancyView.Limited(readDouble())
                OccupancyType.UNLIMITED -> OccupancyView.Unlimited
            }
        }

        internal enum class OccupancyType {
            LIMITED, UNLIMITED;
        }

        internal val OccupancyView.type get() = when(this) {
            is OccupancyView.Limited -> LIMITED
            is OccupancyView.Unlimited -> UNLIMITED
        }
    }
}