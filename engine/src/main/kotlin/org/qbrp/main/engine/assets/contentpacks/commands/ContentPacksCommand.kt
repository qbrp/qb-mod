package org.qbrp.main.engine.assets.contentpacks.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.engine.assets.contentpacks.ContentPackManagerAPI

class ContentPacksCommand(val api: ContentPackManagerAPI): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("cp")
                .then(
                CommandManager.literal("version")
                    .executes { ctx -> ctx.source.sendMessage("<green>Версия: ${api.getVersion()}".asMiniMessage()); 1 }
                        .then(CommandManager.argument("version", StringArgumentType.greedyString())
                            .executes { ctx ->
                                val oldVersion = api.getVersion()
                                api.setVersion(StringArgumentType.getString(ctx, "version"));
                                val newVersion = api.getVersion()
                                ctx.source.sendMessage("<green>Версия изменена: $oldVersion -> $newVersion".asMiniMessage())
                                1
                            }
                        )
                )
                .then(CommandManager.literal("create")
                    .then(CommandManager.argument("version", StringArgumentType.string())
                        .executes { ctx ->
                            try {
                                val version = StringArgumentType.getString(ctx, "version")
                                api.createVersionEntry(version).apply {
                                    buildContentPack()
                                    createManifest()
                                    zipUp()
                                    api.setVersion(version)
                                    ctx.source.sendMessage("<green>Создан контентпак: ${file.path}".asMiniMessage())
                                }
                            } catch (e: Exception) {
                                ctx.source.sendMessage("<red>Ошибка сборки контентпака: ${e.message}".asMiniMessage())
                                e.printStackTrace()
                            }
                            1
                        }))
                .then(CommandManager.literal("generatePatches")
                    .then(CommandManager.argument("version", StringArgumentType.string())
                        .executes { ctx ->
                            try {
                                val versionArg = StringArgumentType.getString(ctx, "version")
                                val version = if (versionArg == "latest") api.getVersion() else versionArg
                                api.generatePatchesToVersion(version)
                                ctx.source.sendMessage("<green>Сгенерированы патчи для версии $version".asMiniMessage())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            1
                        })
                )
        )
    }
}