package org.qbrp.main.engine.inventory.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readContainerId
import org.qbrp.main.core.networking.writeId
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.entity.InventoryParams
import org.qbrp.main.engine.inventory.entity.ScreenCorner
import org.qbrp.main.engine.inventory.networking.WidgetsInventoryListener

class InventoryGroupOpenPacket(val group: InventoryGroup): Packet<WidgetsInventoryListener> {
    constructor(buf: PacketByteBuf) : this(
        InventoryGroup(
            buf.readCollection({ mutableListOf() }, { buf ->
                Inventory(
                    buf.readContainerId(),
                    InventoryParams(
                        buf.readNullable { it.readInt() },
                        buf.readInt()
                    )
                )
            }),
            buf.readEnumConstant<ScreenCorner>(ScreenCorner::class.java)
        )
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeCollection(group.inventories) { buf, elem ->
            buf.writeId(elem.containerId)
            buf.writeNullable(elem.params.maxRows) { buf, value -> buf.writeInt(value) }
            buf.writeInt(elem.params.maxColumns)
        }
        buf.writeEnumConstant(group.screenPos)
    }

    override fun apply(listener: WidgetsInventoryListener) {
        listener.onGroup(group)
    }
}