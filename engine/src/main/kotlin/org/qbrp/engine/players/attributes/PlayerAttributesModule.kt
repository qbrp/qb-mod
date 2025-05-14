package org.qbrp.engine.players.attributes

import PermissionManager.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.prefabs.PrefabField
import org.qbrp.core.mc.player.PlayerManager.getPlayerSession
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload
class PlayerAttributesModule: GameModule("player-attributes"), ServerModCommand {
    override fun onLoad() {
        CommandsRepository.add(this)
    }

    override fun registerComponents(registry: ComponentsRegistry) {
        registry.register(PlayerAttributes::class.java)
        registry.register(PlayerAttributesHandler::class.java)
        gameAPI.getPlayerPrefab().components += PrefabField { PlayerAttributes() }
        gameAPI.getPlayerPrefab().components += PrefabField { PlayerAttributesHandler() }
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("attr")
            .requires { it.player?.hasPermission("pmanager") == true }
            .then(argument("players", EntityArgumentType.players())
                .then(literal("speed")
                    .then(literal("set")
                        .then(argument("value", IntegerArgumentType.integer())
                            .executes() { ctx ->
                                getPlayers(ctx).forEach {
                                    getPlayerSession(it).getComponent<PlayerAttributes>()!!.setSpeed(getValue(ctx))
                                }
                                ctx.source.sendMessage("<green>Скорость изменена".asMiniMessage())
                                1
                            }))
                    .then(literal("reset")
                        .executes() { ctx ->
                            getPlayers(ctx).forEach {
                                getPlayerSession(it).getComponent<PlayerAttributes>()!!.resetSpeed()
                            }
                            ctx.source.sendMessage("<green>Скорость сброшена".asMiniMessage())
                            1
                        }
                    ))))
    }

    fun getPlayers(ctx: CommandContext<ServerCommandSource>): Collection<ServerPlayerEntity> {
        return EntityArgumentType.getPlayers(ctx, "players")
    }

    fun getValue(ctx: CommandContext<ServerCommandSource>): Int {
        return IntegerArgumentType.getInteger(ctx, "value")
    }

}