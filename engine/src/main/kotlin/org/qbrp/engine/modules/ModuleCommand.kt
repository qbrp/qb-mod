package org.qbrp.engine.modules

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.format.Format.asMiniMessage

class ModuleCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("module")
            .then(CommandManager.argument("name", StringArgumentType.word())
            .suggests(ModuleSuggestionProvider())
                .then(CommandManager.argument("script", StringArgumentType.word())
                    .suggests(ModuleScriptsSuggestionProvider())
                    .executes() { ctx ->
                        val script = getScript(ctx)
                        if (script == "disable") {
                            if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).disable()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} отключен".asMiniMessage())
                            }
                        } else if (script == "enable") {
                            if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).enable()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} включен".asMiniMessage())
                            }
                        } else {
                            getModule(ctx).runScript(script).also {
                                ctx.source.sendMessage(it.asMiniMessage())
                            }
                        }
                        1
                    })
            )
        )
    }

    private fun getScript(ctx: CommandContext<ServerCommandSource>): String {
        return StringArgumentType.getString(ctx, "script")
    }

    private fun checkRuntimeStateChanging(ctx: CommandContext<ServerCommandSource>): Boolean {
        if (!getModule(ctx).isRuntimeStateChangeEnabled) {
            ctx.source.sendMessage("<red>Изменения состояния модуля в рантайме недоступно.".asMiniMessage())
            return false
        }
        return true
    }

    private fun getModule(ctx: CommandContext<ServerCommandSource>): QbModule {
        return Engine.moduleManager.getModule<QbModule>(StringArgumentType.getString(ctx, "name"))!!
    }

    private fun getModuleName(ctx: CommandContext<ServerCommandSource>): String {
        return StringArgumentType.getString(ctx, "name")
    }
}