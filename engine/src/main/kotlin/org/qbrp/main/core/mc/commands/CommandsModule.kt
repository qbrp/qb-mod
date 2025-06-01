package org.qbrp.main.core.mc.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.module.Module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule

@Autoload(LoadPriority.HIGHEST)
class CommandsModule: QbModule("commands"), CommandsAPI {
    private var commands = mutableListOf<CommandRegistryEntry>()

    override fun add(commands: List<CommandRegistryEntry>) = commands.forEach { add(it) }
    override fun add(command: CommandRegistryEntry) { commands.add(command) }

    fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach { it.register(dispatcher) }
        commands.clear()
    }

    override fun getKoinModule() = onlyApi<CommandsAPI>(this)
}