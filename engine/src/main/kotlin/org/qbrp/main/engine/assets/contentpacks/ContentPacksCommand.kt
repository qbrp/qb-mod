package org.qbrp.main.engine.assets.contentpacks

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.util.format.Format.formatMm

fun ServerCommandDispatcher.registerContentPacksCommand(api: ContentPackManagerAPI) {
    register(
        CommandManager.literal("cp")
            .then(
                CommandManager.literal("version")
                    .executes { ctx -> ctx.source.sendMessage("<green>Версия: ${api.getVersion()}".formatMm()); 1 }
                    .then(CommandManager.argument("version", StringArgumentType.greedyString())
                            .executes { ctx ->
                                val oldVersion = api.getVersion()
                                api.setVersion(StringArgumentType.getString(ctx, "version"));
                                val newVersion = api.getVersion()
                                ctx.source.sendMessage("<green>Версия изменена: $oldVersion -> $newVersion".formatMm())
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
                                        ctx.source.sendMessage("<green>Создан контентпак: ${file.path}".formatMm())
                                    }
                                } catch (e: Exception) {
                                    ctx.source.sendMessage("<red>Ошибка сборки контентпака: ${e.message}".formatMm())
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
                                    ctx.source.sendMessage("<green>Сгенерированы патчи для версии $version".formatMm())
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                1
                            })
            )
    )
}