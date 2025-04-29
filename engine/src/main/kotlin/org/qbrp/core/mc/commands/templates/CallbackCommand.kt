package org.qbrp.core.mc.commands.templates

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.qbrp.system.utils.format.Format.formatMinecraft

abstract class CallbackCommand {
    fun callback(ctx: CommandContext<ServerCommandSource>, msg: String) {
        ctx.source.sendMessage(msg.formatMinecraft())
    }
    fun callback(ctx: CommandContext<ServerCommandSource>, msg: Text) {
        ctx.source.sendMessage(msg)
    }
}