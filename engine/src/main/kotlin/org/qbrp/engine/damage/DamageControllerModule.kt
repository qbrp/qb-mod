package org.qbrp.engine.damage

import PermissionManager.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import mixin.DamageController
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.DependencyFabric
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule

@Autoload
class DamageControllerModule: QbModule("damage-controller"), DamageControllerAPI, ServerModCommand {
    var enabled: Boolean = true

    override fun load() {
        CommandsRepository.add(this)
    }

    override fun getAPI(): DamageControllerAPI = this

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("ignoredamage")
            .requires { it.player?.hasPermission("damage-controller.ignoredamage") ?: false }
            .then(argument("enabled", BoolArgumentType.bool()))
            .executes { ctx ->
                enabled = BoolArgumentType.getBool(ctx, "enabled")
                1
            }
        )
    }
}