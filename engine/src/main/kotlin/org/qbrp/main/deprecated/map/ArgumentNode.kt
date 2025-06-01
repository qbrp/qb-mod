package org.qbrp.deprecated.map

import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.server.command.ServerCommandSource

interface ArgumentNode {
    val name: String
    val type: String
    val provider: SuggestionProvider<ServerCommandSource>?
    val sub: Boolean
}
