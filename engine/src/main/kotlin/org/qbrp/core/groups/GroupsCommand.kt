package org.qbrp.core.groups

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.SubCommand
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.regions.Regions

@Command("group")
class GroupsCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand
    class Create(@Arg val name: String) {
        @Execute()
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                GroupSelection.createSession(context.source.player!!, "23")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SubCommand
    class Session() {

        @SubCommand
        class Edit(@Arg val name: String) {
            @Execute()
            fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                try {
                    GroupSelection.createSession(context.source.player!!, Groups.getGroup(name)!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @SubCommand
        class Stop() {
            @Execute(operatorLevel = 4)
            fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                GroupSelection.finishSession(context.source.player!!)
            }
        }

        @SubCommand
        class Finish() {
            @Execute(operatorLevel = 4)
            fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                Groups.createGroup(GroupSelection.getSessionGroup(context.source.player!!))
                GroupSelection.finishSession(context.source.player!!)
            }
        }

    }
}