package org.qbrp.main.engine.players.attributes

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.players.model.service.PlayersUtil.getPlayerSession
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.utils.format.Format.formatMm
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.events.PlayerInitAfterJsonLoadEvent

@Autoload
class PlayerAttributesModule: GameModule("player-attributes"), CommandRegistryEntry {
    override fun onLoad() {
        get<CommandsAPI>().add(this)
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PlayerAttributes::class)
                serializeConstructor<PlayerAttributes.Data, ServerPlayerObject, PlayerAttributes> { builder, dto ->
                    PlayerAttributes(builder.componentContext, dto.speed)
                }
                standartConstructor<ServerPlayerObject> {
                    PlayerAttributes(it.componentContext)
                }

                it.register {
                    componentClass(PlayerAttributesHandler::class)
                    standartConstructor<ServerPlayerObject> { PlayerAttributesHandler(it.componentContext) }
                }
            }
        }

        withEventContext {
            on<PlayerInitAfterJsonLoadEvent> {
                it.player.default<PlayerAttributes>()
                it.player.default<PlayerAttributesHandler>()
            }
        }
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
                                    getPlayerSession(it).require<PlayerAttributes>().setSpeed(getValue(ctx))
                                }
                                ctx.source.sendMessage("<green>Скорость изменена".formatMm())
                                1
                            }))
                    .then(literal("reset")
                        .executes() { ctx ->
                            getPlayers(ctx).forEach {
                                getPlayerSession(it).require<PlayerAttributes>().resetSpeed()
                            }
                            ctx.source.sendMessage("<green>Скорость сброшена".formatMm())
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