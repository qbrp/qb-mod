package org.qbrp.core.mc.commands.map

import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.server.command.ServerCommandSource

data class ArgumentNodeImpl(
    override val name: String,
    override val type: String,
    override val sub: Boolean = false,
    override var provider: SuggestionProvider<ServerCommandSource>? = null,
) : ArgumentNode
