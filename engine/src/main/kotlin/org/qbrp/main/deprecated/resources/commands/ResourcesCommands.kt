package org.qbrp.deprecated.resources.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.deprecated.resources.ServerResources

class ResourcesCommands : CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("res")
                .then(
                    literal<ServerCommandSource>("reloadconfig")
                        .requires { source -> source.hasPermissionLevel(4) }
                        .executes { ctx -> executeReload(ctx) }
                )
        )
    }

    private fun executeReload(ctx: CommandContext<ServerCommandSource>): Int {
        val source = ctx.source
        val player = source.player as? ServerPlayerEntity
        if (player == null) {
            source.sendMessage("Только игрок может выполнить эту команду".asMiniMessage())
            return 0
        }
        ServerResources.reloadConfig()
        source.sendMessage("Только игрок может выполнить эту команду".asMiniMessage())
        return 1
    }
}
