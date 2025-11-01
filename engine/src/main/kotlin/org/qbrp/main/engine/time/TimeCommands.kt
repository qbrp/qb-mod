package org.qbrp.main.engine.time

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.deprecated.CommandBuilder
import org.qbrp.deprecated.DependencyFabric
import org.qbrp.deprecated.Deps
import org.qbrp.deprecated.annotations.Arg
import org.qbrp.deprecated.annotations.Command
import org.qbrp.deprecated.annotations.Execute
import org.qbrp.deprecated.annotations.SubCommand
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.commands.templates.CallbackCommand
import org.qbrp.main.core.util.format.Format.formatMm
import org.w3c.dom.Text

fun ServerCommandDispatcher.registerTimeCommands(timeAPI: TimeAPI) = with(timeAPI) {
    register(
        CommandManager.literal("settime")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("hours", IntegerArgumentType.integer(0, 23))
                    .then(
                        CommandManager.argument("minutes", IntegerArgumentType.integer(0, 59))
                            .executes { ctx -> setTime(ctx) }
                    )
            )
    )

    register(
        CommandManager.literal("timeinfo")
            .requires { it.hasPermissionLevel(4) }
            .executes { ctx -> showInfo(ctx) }
    )

    register(
        CommandManager.literal("timeannounce")
            .requires { it.hasPermissionLevel(4) }
            .executes { ctx -> announce(ctx) }
    )
}

context(api: TimeAPI)
private fun setTime(ctx: CommandContext<ServerCommandSource>): Int {
    val hours = IntegerArgumentType.getInteger(ctx, "hours")
    val minutes = IntegerArgumentType.getInteger(ctx, "minutes")

    api.setRpTime(hours * 60 + minutes)
    ctx.source.sendFeedback(
        { "Установлено новое РП-время: ${api.getFormattedRpTime()}".formatMm() },
        true
    )
    return 1
}

context(api: TimeAPI)
private fun showInfo(ctx: CommandContext<ServerCommandSource>): Int {
    val period = api.getCurrentPeriod()
    if (period == null) {
        ctx.source.sendFeedback({ "Сессия закончена.".formatMm() }, false)
        return 0
    }

    ctx.source.sendFeedback(
        {
            "РП: ${api.getFormattedRpTime()}. Прошедшее время: ${api.getGameTime()} мин. Игровое время: ${api.getTickTime()}".formatMm()
        },
        false
    )
    ctx.source.sendFeedback(
        {
            "Период: ${period.name} (${period.duration} мин.)".formatMm()
        },
        false
    )
    return 1
}

context(api: TimeAPI)
private fun announce(ctx: CommandContext<ServerCommandSource>): Int {
    api.broadcastTime()
    ctx.source.sendFeedback({ "Текущее время объявлено.".formatMm() }, false)
    return 1
}