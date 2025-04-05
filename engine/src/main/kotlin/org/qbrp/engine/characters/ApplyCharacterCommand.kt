package org.qbrp.engine.characters

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.lang.NullPointerException

class ApplyCharacterCommand(private val module: CharactersModule): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("character")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .suggests(CharactersSuggestionProvider()).executes { ctx ->
                    val session = PlayerManager.getPlayerSession(ctx.source.player!!)
                    val character = StringArgumentType.getString(ctx, "name")
                    try {
                        if (session.account!!.characters.find { it.name == (character) } != null) {
                            session.account!!.appliedCharacterName = character
                            module.applyCharacter(session)
                        } else {
                            session.entity.sendMessage("<red>Персонаж не найден".asMiniMessage())
                        }
                    } catch (e: Exception) {
                        if (e is NullPointerException) {
                            session.entity.sendMessage("<red>Вы ещё не создали персонажа".asMiniMessage())
                        }
                    }
                    1
                }))
    }
}