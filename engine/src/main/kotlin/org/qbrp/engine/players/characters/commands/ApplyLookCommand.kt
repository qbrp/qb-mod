package org.qbrp.engine.characters

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.engine.players.characters.Character
import org.qbrp.engine.players.characters.appearance.Appearance
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.lang.NullPointerException

class ApplyLookCommand(): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("look")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .suggests(LookSuggestionProvider()).executes { ctx ->
                    val session = PlayerManager.getPlayerSession(ctx.source.player!!)
                    val lookName = StringArgumentType.getString(ctx, "name")
                    session.state.getComponent<Character>()?.data?.let {
                        try {
                            val look = it.appearance.looks.find { it.name == lookName }
                            if (look != null) {
                                session.state.getComponent<Appearance>()!!.apply {
                                    updateLook(look)
                                }
                            } else {
                                session.entity.sendMessage("<red>Облик не найден".asMiniMessage())
                            }
                        } catch (e: Exception) {
                            if (e is NullPointerException) {
                                session.entity.sendMessage("<red>Вы ещё не создали персонажа".asMiniMessage())
                            }
                        }
                    }
                    1
                }))
    }
}