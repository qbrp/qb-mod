package org.qbrp.main.core.mc.server.command

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId

interface ServerCommandExecutor {
    fun executeAsRoot(command: RawMinecraftCommand)
    fun executeAsPlayer(player: MinecraftPlayerId, command: RawMinecraftCommand)
}