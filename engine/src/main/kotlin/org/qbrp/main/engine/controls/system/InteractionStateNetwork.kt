package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.controls.ControlsModule
import org.qbrp.main.engine.controls.networking.InteractionManagerSyncPacket
import org.qbrp.main.engine.players.model.ServerPlayerObject

fun ServerPlayerObject.syncInteractionState(intention: PlayerIntention, packetSender: ServerPacketSender) {
    packetSender.sendImmediately(
        InteractionManagerSyncPacket(intention.intent, intention.alternate),
        ControlsModule.Companion.SYNC_CHANNEL,
        this
    )
}

context(server: ServerContext)
fun ServerPlayerObject.syncInteractionState(intention: PlayerIntention) {
    syncInteractionState(intention, server.packetSender)
}