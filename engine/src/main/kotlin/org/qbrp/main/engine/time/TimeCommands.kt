package org.qbrp.main.engine.time

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.deprecated.CommandBuilder
import org.qbrp.deprecated.DependencyFabric
import org.qbrp.deprecated.Deps
import org.qbrp.deprecated.annotations.Arg
import org.qbrp.deprecated.annotations.Command
import org.qbrp.deprecated.annotations.Execute
import org.qbrp.deprecated.annotations.SubCommand
import org.qbrp.main.core.mc.commands.templates.CallbackCommand

@Command("qbtime")
class TimeCommands(private val api: TimeAPI): org.qbrp.main.core.mc.commands.CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .importDependencies(
                    DependencyFabric()
                        .register("time", api)
                        .createDeps()
                )
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand
    class Set(@Arg val hours: Int,
              @Arg val minutes: Int): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            val time = (deps.get("time") as TimeAPI)

            time.setRpTime(hours * 60 + minutes)
            callback(ctx, "Установлено новое РП-время: ${time.getFormattedRpTime()}")
        }
    }

    @SubCommand
    class Info: CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            val time = (deps.get("time") as TimeAPI)

            val period = time.getCurrentPeriod() ?: return callback(ctx, "Сессия закончена.")
            callback(ctx, "РП: ${time.getFormattedRpTime()}. Прошедшее время: ${time.getGameTime()} минут. Игровое время: ${time.getTickTime()}")
            callback(ctx, "Период: ${period.name} (${period.duration} мин.) ")
        }
    }

    @SubCommand
    class Broadcast: CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            val time = (deps.get("time") as TimeAPI)
            time.broadcastTime()
        }
    }

    @SubCommand
    class Period {

        @SubCommand
        class SetTime(@Arg val newTime: Int): CallbackCommand() {
            @Execute(operatorLevel = 4)
            fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
                val time = (deps.get("time") as TimeAPI)

                val period = time.getCurrentPeriod() ?: return callback(ctx, "Сессия закончена.")
                period.elapsedTimeMinutes = newTime
                callback(ctx, "Установлено новое время для периода $period")
            }
        }
    }
}