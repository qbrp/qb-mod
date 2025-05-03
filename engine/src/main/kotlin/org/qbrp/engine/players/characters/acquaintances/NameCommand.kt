package org.qbrp.engine.players.characters.acquaintances

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.engine.players.characters.Character
import org.qbrp.system.utils.format.Format.asMiniMessage

class NameCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("name")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .executes { ctx ->
                    val player = PlayerManager.getPlayerSession(ctx.source.player ?: return@executes 0)
                    val target = player.getLookingAt() ?: run {
                        ctx.source.sendMessage("<red>Вы не смотрите на персонажа.".asMiniMessage())
                        return@executes 1
                    }
                    val character = PlayerManager.getPlayerSession(target).getComponent<Character>() ?: run {
                        ctx.source.sendMessage("<red>Игрок не применил персонажа.".asMiniMessage())
                        return@executes 1
                    }
                    player.getComponent<NamesPerception>()!!.setName(character, getName(ctx))
                    notify(ctx, getName(ctx), character)
                    1
                }))
        dispatcher.register(CommandManager.literal("selfname")
            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                .executes { ctx ->
                    val player = PlayerManager.getPlayerSession(ctx.source.player ?: return@executes 0)
                    val character = player.getComponent<Character>() ?: run {
                        ctx.source.sendMessage("<red>Игрок не применил персонажа.".asMiniMessage())
                        return@executes 1
                    }
                    player.getComponent<NamesPerception>()!!.setName(character, getName(ctx))
                    notify(ctx, getName(ctx), character)
                    1
                }))
        dispatcher.register(CommandManager.literal("ignorename")
                .executes { ctx ->
                    val player = PlayerManager.getPlayerSession(ctx.source.player ?: return@executes 0)
                    val ignore = !player.getComponent<NamesPerception>()!!.ignore
                    player.getComponent<NamesPerception>()!!.ignore = ignore
                    val text = "Переименование имени ${if (ignore) "запрещено" else "разрешено"}"
                    ctx.source.sendMessage("<green>$text".asMiniMessage())
                    1
                })
    }
    private fun notify(ctx: CommandContext<ServerCommandSource>, nick: String, character: Character) {
        ctx.source.sendMessage("<gray>Установлено имя ${character.data.getTextWithColorTag(nick)}".asMiniMessage())
    }
    private fun getName(ctx: CommandContext<ServerCommandSource>): String = StringArgumentType.getString(ctx, "name")
}