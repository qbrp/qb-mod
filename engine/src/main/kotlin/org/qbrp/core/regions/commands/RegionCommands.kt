package org.qbrp.core.regions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.regions.Cuboid
import org.qbrp.core.regions.RegionSelectionProcessor
import org.qbrp.core.regions.Regions

class RegionCommands: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("reg")
            .then(CommandManager.literal("remove"))
            .then(CommandManager.literal("add")
                .then(CommandManager.argument("name", StringArgumentType.string())
                .executes { context ->
                    Regions.createRegion(
                        StringArgumentType.getString(context, "name"),
                        RegionSelectionProcessor.getPlayerSelection(context.source.player as ServerPlayerEntity).convertToCuboid() as Cuboid)
                    1
                }))
        )
    }
}