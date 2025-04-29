package org.qbrp.core.mc.registry

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

object CommandsRepository {
    private var commands = mutableListOf<ServerModCommand>()

    fun add(commands: List<ServerModCommand>) = commands.forEach { add(it) }
    fun add(command: ServerModCommand) = commands.add(command)

    fun initCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach { it.register(dispatcher) }
        commands.clear()
    }
}