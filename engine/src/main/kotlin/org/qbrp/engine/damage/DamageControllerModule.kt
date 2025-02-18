package org.qbrp.engine.damage

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.DependencyFabric
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine

class DamageControllerModule: ServerModCommand {
    var state: Boolean = true

    fun load() {
        CommandsRepository.add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .importDependencies(
                    DependencyFabric()
                        .register("state", state)
                        .createDeps()
                )
                .printErrors()
                .buildTree(DamageCommand::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @Command("ignoredamage")
    class DamageCommand(@Arg("boolean") val state: Boolean): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Engine.damageControllerModule.state = state
            callback(context, "Урон ${if (state) "включен" else "выключен"}")
        }
    }

}