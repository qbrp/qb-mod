package org.qbrp.core.regions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.Provider
import org.qbrp.core.game.commands.annotations.SubCommand
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.regions.model.Cuboid
import org.qbrp.core.regions.RegionSelection
import org.qbrp.core.regions.Regions
import org.qbrp.core.regions.model.RegionView
import org.qbrp.system.utils.format.Format.formatMinecraft

@Command("qbrg")
class RegionCommands: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand
    class Add(@Arg(type = "word") val name: String): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Regions.createRegion(name, RegionSelection.getPlayerSelection(context.source.player as ServerPlayerEntity).convertToCuboid() as Cuboid)
            callback(context, "Создан регион $name")
        }
    }

    @SubCommand
    class Square(
        @Arg(type = "word") val name: String,
        @Arg val size: Int): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val p = context.source.player!!.pos
            Regions.createRegion(name, Cuboid(p.x.toInt() - size, p.y.toInt() - size, p.z.toInt() - size,
                p.x.toInt() + size, p.y.toInt() + size, p.z.toInt() + size) as Cuboid)
            callback(context, "Создан регион $name")
        }
    }

    @SubCommand
    class Remove(@Provider(Regions.RegionsProvider::class) @Arg(type = "word") val name: String): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Regions.removeRegion(name)
            callback(context, "Удалён регион $name")
        }
    }

    @SubCommand
    class Nearest: CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Regions.nearest(context.source.position, 10).forEach {
                callback(context,RegionView(it, context.source.player!!).getText())
            }
        }
    }
}