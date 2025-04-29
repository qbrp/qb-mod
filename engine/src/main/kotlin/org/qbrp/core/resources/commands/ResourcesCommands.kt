package org.qbrp.core.resources.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.commands.CommandBuilder
import org.qbrp.core.mc.commands.Deps
import org.qbrp.core.mc.commands.annotations.Command
import org.qbrp.core.mc.commands.annotations.Execute
import org.qbrp.core.mc.commands.annotations.SubCommand
import org.qbrp.core.mc.commands.templates.CallbackCommand
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources

@Command("res")
class ResourcesCommands: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand
    class ReloadConfig: CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            ServerResources.reloadConfig()
            callback(ctx, "&aКонфигурация перезагружена")
        }
    }
}