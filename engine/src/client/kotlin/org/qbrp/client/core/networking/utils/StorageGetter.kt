package org.qbrp.client.core.networking.utils

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.networking.synchronization.Resolver
import org.qbrp.main.core.networking.synchronization.SynchronizePacket

open class StorageGetter<O: Identifiable>(protected val storage: Storage<O>): Resolver<O> {
    open fun onFound(packet: SynchronizePacket<O>, entity: O) {}

    override fun resolve(packet: SynchronizePacket<O>): O? {
        return storage.getById(packet.id)?.apply {
            onFound(packet, this)
        }
    }

    override fun onPacket(p: Packet<PacketListener>) {
        p.apply(this)
    }
}