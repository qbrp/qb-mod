package org.qbrp.core.game.registry

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

class CommandsRepository {
    private var commands = mutableListOf<ServerModCommand>()

    fun add(command: ServerModCommand) = commands.add(command)

    fun initCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach { it.register(dispatcher) }
    }
}