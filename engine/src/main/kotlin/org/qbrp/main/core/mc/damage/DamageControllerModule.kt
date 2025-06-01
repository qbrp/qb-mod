package org.qbrp.main.core.mc.damage

import PermissionsUtil.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

@Autoload
class DamageControllerModule: QbModule("damage-controller"), DamageControllerAPI, CommandRegistryEntry {
    var enabled: Boolean = true

    override fun onLoad() {
        get<CommandsAPI>().add(this)
    }

    override fun getAPI(): DamageControllerAPI = this

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("ignoredamage")
            .requires { it.player?.hasPermission("damage-controller.ignoredamage") == true }
            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                .executes { ctx ->
                    enabled = BoolArgumentType.getBool(ctx, "enabled")
                    1
                })
        )
    }

    override fun isEnabled(): Boolean = enabled
}