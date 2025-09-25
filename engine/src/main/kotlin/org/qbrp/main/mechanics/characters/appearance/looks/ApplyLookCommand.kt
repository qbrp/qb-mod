package org.qbrp.main.mechanics.characters.appearance.looks

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.players.model.service.PlayersUtil
import org.qbrp.main.engine.characters.LookSuggestionProvider
import java.lang.NullPointerException

class ApplyLookCommand(): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("look")
            .then(CommandManager.argument("label", StringArgumentType.greedyString())
                .suggests(LookSuggestionProvider()).executes { ctx ->
                    val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
                    val lookName = StringArgumentType.getString(ctx, "label")
                    session.get<LookManager>()?.let {
                        try {
                            val success = it.updateLook(lookName)
                            session.sendChatMessage(if (success) "<gray>Применён облик ${it.getFormattedName()}" else "<red>Облик не найден")
                        } catch (e: Exception) {
                            if (e is NullPointerException) {
                                session.sendChatMessage("<red>Вы ещё не создали персонажа")
                            }
                        }
                    }
                    1
                }))
    }
}