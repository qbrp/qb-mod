package org.qbrp.main.core.mc.server.command

import net.minecraft.server.MinecraftServer
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.server.ServerModule
import org.qbrp.main.core.networking.sender.ServerPacketSender

class ServerCommandExecutorImpl(private val packetSender: ServerPacketSender,
                                private val server: MinecraftServer,
                                private val playerManager: MinecraftPlayerManager): ServerCommandExecutor {
    override fun executeAsRoot(command: RawMinecraftCommand) {
        server.commandManager.executeWithPrefix(server.commandSource, command.value)
    }

    override fun executeAsPlayer(player: MinecraftPlayerId, command: RawMinecraftCommand) {
        packetSender.sendImmediately(
            CommandExecutePacket(command),
            ServerModule.COMMAND_EXECUTE_CHANNEL,
            player.getEntity(playerManager)
        )
    }
}