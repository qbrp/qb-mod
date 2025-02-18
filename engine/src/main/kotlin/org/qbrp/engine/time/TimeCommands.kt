package org.qbrp.engine.time

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.SubCommand
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine

@Command("qbtime")
class TimeCommands: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand
    class Set(@Arg val hours: Int,
              @Arg val minutes: Int): CallbackCommand() {
        val time = Engine.timeModule.API

        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            time.setRpTime(hours * 60 + minutes)
            callback(ctx, "Установлено новое РП-время: ${time.getFormattedRpTime()}")
        }
    }

    @SubCommand
    class Enabled(@Arg val state: Boolean): CallbackCommand() {
        val time = Engine.timeModule

        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            time.enabled = state
            callback(ctx, "Модуль времени ${if (!state) "выключен" else "включен"}")
        }
    }

    @SubCommand
    class Info: CallbackCommand() {
        val time = Engine.timeModule.API

        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            val period = time.getCurrentPeriod() ?: return callback(ctx, "Сессия закончена.")
            callback(ctx, "РП: ${time.getFormattedRpTime()}. Прошедшее время: ${time.getGameTime()} минут. Игровое время: ${time.getTickTime()}")
            callback(ctx, "Период: ${period.name} (${period.duration} мин.) ")
        }
    }

    @SubCommand
    class Period {

        @SubCommand
        class SetTime(@Arg val newTime: Int): CallbackCommand() {
            val time = Engine.timeModule.API

            @Execute(operatorLevel = 4)
            fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
                val period = time.getCurrentPeriod() ?: return callback(ctx, "Сессия закончена.")
                period.elapsedTimeMinutes = newTime
                callback(ctx, "Установлено новое время для периода $period")
            }
        }
    }
}