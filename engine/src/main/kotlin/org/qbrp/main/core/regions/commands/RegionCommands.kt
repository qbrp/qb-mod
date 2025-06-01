package org.qbrp.main.core.regions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.fabricmc.fabric.api.registry.CommandRegistry
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.regions.model.Cuboid
import org.qbrp.main.core.regions.model.RegionView
import org.qbrp.main.core.regions.RegionSelection
import org.qbrp.main.core.regions.RegionsAPI
import org.qbrp.main.core.utils.format.Format.asMiniMessage

class RegionCommands(
    private val regions: RegionsAPI,
    private val selection: RegionSelection
): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("qbrg")
                .requires { it.hasPermissionLevel(4) }

                .then(
                    literal<ServerCommandSource>("add")
                        .then(argument<ServerCommandSource, String>("name", StringArgumentType.word())
                            .executes { ctx ->
                                val player = ctx.source.player!!
                                val name = StringArgumentType.getString(ctx, "name")
                                val cuboid = selection.getPlayerSelection(player).convertToCuboid() as Cuboid
                                regions.createRegion(name, cuboid)
                                ctx.source.sendMessage("<green>Регион «$name» создан</green>".asMiniMessage())
                                1
                            }
                        )
                )

                .then(
                    literal<ServerCommandSource>("square")
                        .then(argument<ServerCommandSource, String>("name", StringArgumentType.word())
                            .then(argument<ServerCommandSource, Int>("size", IntegerArgumentType.integer(1))
                                .executes { ctx ->
                                    val player = ctx.source.player!!
                                    val name = StringArgumentType.getString(ctx, "name")
                                    val size = IntegerArgumentType.getInteger(ctx, "size")
                                    val pos = player.pos
                                    val cuboid = Cuboid(
                                        pos.x.toInt() - size, pos.y.toInt() - size, pos.z.toInt() - size,
                                        pos.x.toInt() + size, pos.y.toInt() + size, pos.z.toInt() + size
                                    )
                                    regions.createRegion(name, cuboid)
                                    ctx.source.sendMessage("<green>Квадратный регион «$name» со стороной $size создан</green>".asMiniMessage())
                                    1
                                }
                            )
                        )
                )

                .then(
                    literal<ServerCommandSource>("remove")
                        .then(argument<ServerCommandSource, String>("name", StringArgumentType.word())
                            .executes { ctx ->
                                val name = StringArgumentType.getString(ctx, "name")
                                if (!regions.removeRegion(name)) {
                                    ctx.source.sendMessage("<red>Регион «$name» не найден</red>".asMiniMessage())
                                    return@executes 0
                                }
                                ctx.source.sendMessage("<green>Регион «$name» удалён</green>".asMiniMessage())
                                1
                            }
                        )
                )

                .then(
                    literal<ServerCommandSource>("nearest")
                        .executes { ctx ->
                            val player = ctx.source.player!!
                            val nearby = regions.nearest(ctx.source.position, 10)
                            if (nearby.isEmpty()) {
                                ctx.source.sendMessage("<yellow>В радиусе 10 блоков нет регионов</yellow>".asMiniMessage())
                                return@executes 1
                            }
                            nearby.forEach {
                                ctx.source.sendMessage(RegionView(it, player).getText())
                            }
                            1
                        }
                )
        )
    }
}
