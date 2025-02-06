package org.qbrp.engine.spectators.respawn

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.registry.ServerModCommand

class SpawnCommand(val respawnManager: RespawnManager): ServerModCommand {

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("qbs")
            .executes { context ->
                respawnManager.spawn(context.source.player!!)
                1
            }
        )
    }
}