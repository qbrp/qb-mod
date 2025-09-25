package org.qbrp.main.core.mc.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.HIGHEST)
class CommandsModule: EngineModule("commands"), CommandsAPI {
    private var commands = mutableListOf<CommandRegistryEntry>()

    override fun onEnable() {
        ServerLifecycleEvents.SERVER_STARTED.register {
            registerCommands(it.commandManager.dispatcher)
        }
    }

    override fun add(commands: List<CommandRegistryEntry>) = commands.forEach { add(it) }
    override fun add(command: CommandRegistryEntry) { commands.add(command) }

    fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach { it.register(dispatcher) }
        commands.clear()
    }

    override fun getKoinModule() = onlyApi<CommandsAPI>(this)
}