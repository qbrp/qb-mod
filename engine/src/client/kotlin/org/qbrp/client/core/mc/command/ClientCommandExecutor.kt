package org.qbrp.client.core.mc.command

import net.minecraft.client.MinecraftClient
import org.qbrp.client.core.mc.exception.PlayerNotOnServerException
import org.qbrp.main.core.mc.server.command.PlayerCommandExecuteListener
import org.qbrp.main.core.mc.server.command.RawMinecraftCommand

class ClientCommandExecutor(private val client: MinecraftClient): PlayerCommandExecuteListener {
    override fun onCommand(command: RawMinecraftCommand) {
        (client.player?.networkHandler ?: throw PlayerNotOnServerException()).sendChatCommand(command.value)
    }
}