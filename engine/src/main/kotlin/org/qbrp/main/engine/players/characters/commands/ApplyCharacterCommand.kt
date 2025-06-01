package org.qbrp.main.engine.characters

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.players.characters.CharactersModule
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import java.lang.NullPointerException

class ApplyCharacterCommand(private val module: CharactersModule): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("character")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .suggests(CharactersSuggestionProvider()).executes { ctx ->
                    val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
                    val characterName = StringArgumentType.getString(ctx, "name")
                    try {
                        val character = session.account.characters.find { it.name == (characterName) }
                        if (character != null) {
                            module.setCharacter(session, character)
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