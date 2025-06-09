package org.qbrp.main.core.utils.networking.messaging

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.PlayersUtil

data class ServerReceiverContext(
    val id: String,
    val server: MinecraftServer,
    val player: ServerPlayerEntity,
    val handler: ServerPlayPacketListener,
    val responseSender: PacketSender
): ReceiverContext {
    val playerObj get() = PlayersUtil.getPlayerSession(player)
}