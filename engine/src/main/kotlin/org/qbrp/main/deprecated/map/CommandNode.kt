package org.qbrp.deprecated.map

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

interface CommandNode {
    var name: String
    val subCommands: MutableList<CommandNode>
    val arguments: MutableList<ArgumentNode>
    var execute: Executor?
    fun getLiteral(): LiteralArgumentBuilder<ServerCommandSource>
}