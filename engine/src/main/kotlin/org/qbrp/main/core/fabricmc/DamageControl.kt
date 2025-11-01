package org.qbrp.main.core.fabricmc

import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.server.command.CommandManager
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.commands.getBoolean
import org.qbrp.main.core.fabricmc.commands.registerCommand
import org.qbrp.main.core.fabricmc.player.hasPermission

object DamageControllerMixinAccess {
    var enabled: Boolean = true
}

fun ServerCommandDispatcher.registerDamageControlCommand() {
    register(CommandManager.literal("ignoredamage")
        .requires { it.player?.hasPermission("damage-controller.ignoredamage") == true }
        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                .executes { ctx ->
                    DamageControllerMixinAccess.enabled = ctx.getBoolean("enabled")
                    1
                }
        )
    )
}