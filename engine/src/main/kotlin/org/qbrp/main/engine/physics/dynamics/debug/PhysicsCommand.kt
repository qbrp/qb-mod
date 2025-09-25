package org.qbrp.main.engine.physics.dynamics.debug

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.physics.dynamics.chunk.loader.ChunkLoadDebugger
import org.qbrp.main.engine.physics.dynamics.simulation.PhysicsSimulation
import org.qbrp.main.engine.props.world.WorldPropManager

class PhysicsCommand(private val worldPropManager: WorldPropManager,
                     private val chunkLoadDebugger: ChunkLoadDebugger,
                     private val simulation: PhysicsSimulation): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("physics")
            .then(CommandManager.literal("allow-sleep")
                .then(CommandManager.argument("enable", BoolArgumentType.bool())
                    .executes { ctx -> allowSleep(ctx)})
            )
            .then(CommandManager.literal("chunk-load-particles")
                .then(CommandManager.argument("enable", BoolArgumentType.bool())
                    .executes { ctx -> chunkLoadParticles(ctx)})
            )
            .then(CommandManager.literal("debug-log")
                .then(CommandManager.argument("enable", BoolArgumentType.bool())
                    .executes { ctx -> debugLog(ctx)})
            )
        )
    }

    private fun allowSleep(ctx: CommandContext<ServerCommandSource>): Int {
        val allowSleep = BoolArgumentType.getBool(ctx, "enable")
        worldPropManager.props.forEach { it.body.allowSleep = allowSleep }
        return 1
    }

    private fun debugLog(ctx: CommandContext<ServerCommandSource>): Int {
        val enable = BoolArgumentType.getBool(ctx, "enable")
        simulation.debugLog = enable
        return 1
    }

    private fun chunkLoadParticles(ctx: CommandContext<ServerCommandSource>): Int {
        val enable = BoolArgumentType.getBool(ctx, "enable")
        chunkLoadDebugger.enabled = enable
        return 1
    }
}