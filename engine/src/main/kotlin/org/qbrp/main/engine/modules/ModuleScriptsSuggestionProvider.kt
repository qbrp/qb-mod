package org.qbrp.main.engine.modules

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.modules.QbModule
import java.util.concurrent.CompletableFuture

class ModuleScriptsSuggestionProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val module = Engine.getModule<QbModule>(StringArgumentType.getString(context, "name"))!!

        if (module.isDynamicLoadingAllowed) {
            builder.suggest("reload")
            builder.suggest("load")
            builder.suggest("unload")
        }
        if (module.isDynamicActivationAllowed) {
            builder.suggest("disable")
            builder.suggest("enable")
        }
        module.listScripts().forEach {
            builder.suggest(it)
        }

        return builder.buildFuture()
    }
}
