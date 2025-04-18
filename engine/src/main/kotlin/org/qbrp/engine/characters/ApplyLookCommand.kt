package org.qbrp.engine.characters

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.lang.NullPointerException

class ApplyLookCommand(private val module: CharactersModule): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("look")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .suggests(LookSuggestionProvider()).executes { ctx ->
                    val session = PlayerManager.getPlayerSession(ctx.source.player!!)
                    val lookName = StringArgumentType.getString(ctx, "name")
                    val character = session.account!!.appliedCharacter
                    character?.let {
                        try {
                            val look = it.appearance.looks.find { it.name == lookName }
                            if (look != null) {
                                module.applyLook(session, look)
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