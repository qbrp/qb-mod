package org.qbrp.client.engine.items.networking

import org.qbrp.client.core.networking.utils.StorageGetter
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.main.core.networking.synchronization.SynchronizePacket
import org.qbrp.main.engine.items.service.ItemStorage

open class ItemSyncResolver(storage: ItemStorage<ClientItemObject>): StorageGetter<ClientItemObject>(storage) {
    override fun resolve(packet: SynchronizePacket<ClientItemObject>): ClientItemObject? {
        return super.resolve(packet) ?: run {
            val item = ClientItemObject(packet.id)
            item.state.parent = item
            storage.add(item)
            onFound(packet, item)
            return item
        }
    }
}