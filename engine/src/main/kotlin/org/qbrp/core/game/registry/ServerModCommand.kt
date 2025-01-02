package org.qbrp.core.game.registry

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

interface ServerModCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>)
}