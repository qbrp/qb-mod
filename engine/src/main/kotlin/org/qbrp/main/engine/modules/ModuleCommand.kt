package org.qbrp.main.engine.modules

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.utils.format.Format.formatMm

class ModuleCommand: CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("module")
            .then(CommandManager.argument("label", StringArgumentType.word())
            .suggests(ModuleSuggestionProvider())
                .then(CommandManager.argument("script", StringArgumentType.word())
                    .suggests(ModuleScriptsSuggestionProvider())
                    .executes() { ctx ->


                        val script = getScript(ctx)
                        when (script) {
                            "disable" -> if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).disable()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} отключен".formatMm())
                            }
                            "enable" -> if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).enable()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} включен".formatMm())
                            }
                            "load" -> if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).load()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} загружен".formatMm())
                            }
                            "unload" -> if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).unload()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} выгружен".formatMm())
                            }
                            "reload" -> if (checkRuntimeStateChanging(ctx)) {
                                getModule(ctx).reload()
                                ctx.source.sendMessage("<green>Модуль ${getModuleName(ctx)} перезагружен".formatMm())
                            }
                            else -> {
                                getModule(ctx).runScript(script).also {
                                    ctx.source.sendMessage(it.formatMm())
                                }
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
        if (!getModule(ctx).isDynamicActivationAllowed) {
            ctx.source.sendMessage("<red>Изменения состояния модуля в рантайме недоступно.".formatMm())
            return false
        }
        return true
    }

    private fun getModule(ctx: CommandContext<ServerCommandSource>): EngineModule {
        return Engine.getModule<EngineModule>(StringArgumentType.getString(ctx, "label"))!!
    }

    private fun getModuleName(ctx: CommandContext<ServerCommandSource>): String {
        return StringArgumentType.getString(ctx, "label")
    }
}