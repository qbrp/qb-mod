package org.qbrp.main.network.inventory

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.StringId
import org.qbrp.main.core.util.toId
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.entity.OccupancyView
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.network.transport.readContainerId
import org.qbrp.main.network.transport.readEnum
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import org.qbrp.main.network.transport.writeEnum
import org.qbrp.main.network.transport.writeId

data class ContainerEntryReference(override val stringId: StringId) : Identifiable {
    override val id: String get() = stringId.value
}

val CONTAINER_SYNC_ENDPOINT = NetworkChannel<ContainerSyncPacket>(
    codec = PacketCodec.Binary(
        {
            ContainerSyncPacket(
                readContainerId(),
                readEntries(),
                ContainerDisplayName(readString()),
                readOccupancy()
            )
        },
        {
            writeId(it.containerId)
            writeEntries(it.entries)
            writeString(it.name.value)
            writeOccupancy(it.occupancy)
        }
    )
).wrapEndpoint()

data class ContainerSyncPacket(
    val containerId: ContainerId,
    val entries: List<ContainerEntryReference>,
    val name: ContainerDisplayName,
    val occupancy: OccupancyView
) : Packet

val INVENTORY_GROUP_OPEN_ENDPOINT = NetworkChannel<InventoryGroupOpenPacket>(
    codec = PacketCodec.Binary(
        {
            InventoryGroupOpenPacket(
                InventoryGroup(
                    readCollection({ mutableListOf() }, { buf ->
                        Inventory(
                            buf.readContainerId(),
                            InventoryParams(
                                buf.readNullable { it.readInt() },
                                buf.readInt()
                            )
                        )
                    }),
                    readEnum()
                )
            )
        },
        {
            writeCollection(it.group.inventories) { buf, elem ->
                buf.writeId(elem.containerId)
                buf.writeNullable(elem.params.maxRows) { buf, value -> buf.writeInt(value) }
                buf.writeInt(elem.params.maxColumns)
            }
            writeEnumConstant(it.group.screenPos)
        }
    )
).wrapEndpoint()

data class InventoryGroupOpenPacket(val group: InventoryGroup) : Packet

val INVENTORY_CLOSE_ENDPOINT = NetworkChannel<InventoryClosePacket>(
    codec = PacketCodec.Binary(
        { InventoryClosePacket(ContainerId(readString())) },
        { writeId(it.id) }
    )
).wrapEndpoint()

data class InventoryClosePacket(val id: ContainerId) : Packet

// Utils

fun PacketByteBuf.writeEntries(entries: List<ContainerEntryReference>) {
    writeCollection(entries) { buf, elem -> writeId(elem) }
}

fun PacketByteBuf.readEntries(): List<ContainerEntryReference> {
    return readCollection(
        { mutableListOf() },
        { buf -> ContainerEntryReference(buf.readString().toId()) }
    )
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

internal val OccupancyView.type
    get() = when(this) {
        is OccupancyView.Limited -> OccupancyType.LIMITED
        is OccupancyView.Unlimited -> OccupancyType.UNLIMITED
    }
