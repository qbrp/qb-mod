package org.qbrp.client.engine.items

import net.minecraft.item.ItemStack
import org.qbrp.client.engine.inventory.model.ClientInventoryEntry
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.core.synchronization.components.C2SMessaging
import org.qbrp.main.core.synchronization.components.MessagingChannelSender
import org.qbrp.main.core.synchronization.state.SynchronizeUpdate
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry

class ClientItemObject(override val id: String,
                       override var itemStack: ItemStack?,
                       override val messageSender: MessagingChannelSender
): ItemObject(), C2SMessaging, SynchronizeUpdate, ClientInventoryEntry {
    override fun update(cluster: ClusterViewer) {
        itemStack = cluster.getComponentData("itemstack")
    }

    companion object {
        val ITEMSTACK_ENTRY = ClusterEntry<ItemStack>("itemstack")
        fun deserialize(cluster: ClusterViewer, channel: MessagingChannelSender): ClientItemObject {
            return ClientItemObject(
                cluster.getEntry(IDENTIFIER_ENTRY)!!,
                cluster.getEntry(ITEMSTACK_ENTRY),
                channel
            )
        }
    }
}