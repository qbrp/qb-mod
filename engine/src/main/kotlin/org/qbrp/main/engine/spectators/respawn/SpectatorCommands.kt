package org.qbrp.main.engine.spectators.respawn

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.asReference
import org.qbrp.main.core.utils.format.Format.formatMm

class SpectatorCommands(private val respawnManager: RespawnManager, private val context: MinecraftContext): CommandRegistryEntry, KoinComponent {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) { with(context) {
        val mcCtx: MinecraftContext by inject()
        dispatcher.register(
            CommandManager.literal("spawn").executes { ctx ->
                with(mcCtx) { ctx.source.player?.asReference()?.let { respawnManager.spawn(it) } }
                1
            }
        )
        dispatcher.register(
            CommandManager.literal("ignoreqbs").executes { ctx ->
                ctx.source.player?.asReference()?.let { respawnManager.ignore(it) }
                1
            }
        )
        dispatcher.register(
            CommandManager.literal("giveqbs")
            .then(
                CommandManager.argument("player", EntityArgumentType.players())
                    .executes { ctx ->
                        val players = EntityArgumentType.getPlayers(ctx, "player")
                        players.forEach { player ->
                            get<RespawnManager>().giveSpectator(player.asReference())
                        }
                        ctx.source.sendMessage("Режим спавна назначен игрокам ${players.joinToString(", ") { it.name.string }}".formatMm())
                        1
                    }
            )
        )
    }
    }
}