package org.qbrp.client.engine.players.networking

import org.qbrp.client.core.networking.utils.StorageGetter
import org.qbrp.client.engine.players.ClientPlayerStorage
import org.qbrp.client.engine.players.ClientPlayerObject
import org.qbrp.client.engine.players.event.PlayerLoadEvent
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.networking.synchronization.SynchronizePacket
import org.qbrp.main.engine.players.model.networking.packet.PlayerSyncPacket
import org.qbrp.main.engine.players.model.util.PlayerId

open class PlayerResolver(
    private val eventContext: EventContext,
    private val factory: ClientPlayerFactory,
    storage: ClientPlayerStorage
) : StorageGetter<ClientPlayerObject>(storage) {
    override fun resolve(packet: SynchronizePacket<ClientPlayerObject>): ClientPlayerObject? {
        return super.resolve(packet) ?: run {
            packet as PlayerSyncPacket
            val player = factory.build(PlayerId(packet.id))
            storage.add(player)
            onFound(packet, player)
            with(eventContext) { PlayerLoadEvent(player).emit() }
            return player
        }
    }
}