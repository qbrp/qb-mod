package org.qbrp.main.engine.physics.player

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.minecraft.server.command.CommandManager
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.commands.getDouble
import org.qbrp.main.core.fabricmc.commands.getFloat
import org.qbrp.main.core.fabricmc.commands.getInt
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.getChunkPos
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.players.model.engine
import org.qbrp.main.engine.players.model.pos

fun ServerCommandDispatcher.registerMoveCommand() {
    register(
        literal("move")
            .then(
                doubleArgument("x")
                    .then(
                        doubleArgument("y")
                            .then(
                                doubleArgument("z")
                                    .executes { ctx ->
                                        try {
                                            val enginePlayer = ctx.source.player?.engine() ?: return@executes 0
                                            val oldPos = enginePlayer.pos
                                            val x = ctx.getDouble("x")
                                            val y = ctx.getDouble("y")
                                            val z = ctx.getDouble("z")
                                            enginePlayer.add(
                                                TeleportationComponent(
                                                    oldPos.x + x,
                                                    oldPos.y + y,
                                                    oldPos.z + z,
                                                    true
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        1
                                    }
                            )
                    )
            )
    )
}

object EngineTeleportCommandsMixinAccess {
    fun registerTeleportCommand(dispatcher: ServerCommandDispatcher) {
        dispatcher.registerMoveCommand()
    }
}

fun literal(name: String) = CommandManager.literal(name)

fun floatArgument(name: String) = CommandManager.argument(name, FloatArgumentType.floatArg())

fun doubleArgument(name: String) = CommandManager.argument(name, DoubleArgumentType.doubleArg())