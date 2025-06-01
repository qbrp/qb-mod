package org.qbrp.main.engine.players.attributes

import PermissionsUtil.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.player.PlayersUtil.getPlayerSession
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.utils.Deps
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload
class PlayerAttributesModule: GameModule("player-attributes"), CommandRegistryEntry {
    override fun onLoad() {
        get<CommandsAPI>().add(this)
    }

    override fun registerComponents(registry: ComponentsRegistry) {
        registry.register(PlayerAttributes::class.java)
        registry.register(PlayerAttributesHandler::class.java)
        Deps.PLAYER_PREFAB.components += PrefabField { PlayerAttributes() }
        Deps.PLAYER_PREFAB.components += PrefabField { PlayerAttributesHandler() }
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