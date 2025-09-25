package org.qbrp.main.core.networking.sender

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.acknowledge.PacketSendTask

interface ServerPacketSender {
    fun sendImmediately(packet: Packet<*>, channelName: String, player: PlayerReference)
    fun sendImmediately(packet: Packet<*>, channelName: String, player: ServerPlayerEntity)
    fun send(packet: Packet<*>, channelName: String, player: PlayerReference): PacketSendTask
}