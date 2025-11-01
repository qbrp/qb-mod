package org.qbrp.main.core.fabricmc

import net.minecraft.server.MinecraftServer
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.fabricmc.server.CommandActor
import org.qbrp.main.core.fabricmc.server.CommandHolder
import org.qbrp.main.core.util.drain
import org.qbrp.main.engine.world.model.World
import org.qbrp.main.network.player.sendCommandExecutePacket

class WorldSystem(private val server: MinecraftServer) {
    private val commandManager = server.commandManager

    fun update(world: World) {
        world.get<CommandHolder>()?.commands?.drain { commandInvoke ->
            val actor = commandInvoke.actor
            val command = commandInvoke.command.value
            when (actor) {
                is CommandActor.Player -> {
                    actor.player.sendCommandExecutePacket(command)
                }

                is CommandActor.Root -> {
                    commandManager.executeWithPrefix(server.commandSource, command)
                }
            }
        }
    }
}