package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readContainerId
import org.qbrp.main.core.networking.readItem
import org.qbrp.main.core.networking.writeId
import org.qbrp.main.core.networking.writeItem
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

class InteractCallPacket(val hoveredInventoryId: ContainerId?, val selectedItem: ItemObject?, val player: PlayerObject? = null): Packet<ServerInteractionListener> {
    constructor(buf: PacketByteBuf, player: PlayerObject, itemStorage: ItemStorage<out ItemObject>) : this(
        buf.readNullable<ContainerId> { buf -> buf.readContainerId() },
        buf.readNullable<ItemObject>({ buf -> buf.readItem(itemStorage) }),
        player
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeNullable(hoveredInventoryId, { buf, id -> buf.writeId(id)})
        buf.writeNullable(selectedItem, { buf, item -> buf.writeItem(item) })
    }

    override fun apply(listener: ServerInteractionListener) {
        listener.onListRequest(player as ServerPlayerObject, InteractionRequestContext(player, hoveredInventoryId, selectedItem))
    }
}