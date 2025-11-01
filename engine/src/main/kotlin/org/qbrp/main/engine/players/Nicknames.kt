package org.qbrp.main.engine.players

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.Serializable
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.engineOrNull

fun ServerCommandDispatcher.registerNicknameCommand() {
    register(
        CommandManager.literal("nick")
            .then(
                CommandManager.argument("nickname", StringArgumentType.greedyString())
                    .executes { ctx ->
                        val player = ctx.source.player?.engineOrNull() ?: return@executes 0
                        val nickname = ctx.getNickname()
                        player.customName = nickname
                        ctx.source.sendMessage("<gray>Никнейм установлен на ${nickname}".formatMm())
                        1
                    }
            )
    )
    register(
        CommandManager.literal("resetnick")
            .executes { ctx ->
                val player = ctx.source.player?.engineOrNull() ?: return@executes 0
                player.removeCustomName()
                ctx.source.sendMessage("<gray>Никнейм сброшен.".formatMm())
                1
            }
    )
}

private fun CommandContext<ServerCommandSource>.getNickname(): String {
    return StringArgumentType.getString(this, "nickname")
}

@Serializable
data class DisplayName(val username: Username, var custom: String?) : DataComponent {
    val name: String
        get() = custom ?: username.value
}

fun PlayerObject.removeCustomName() {
    get<DisplayName>()?.custom = null
}

var PlayerObject.customName
    get() = get<DisplayName>()?.custom
    set(value) {
        get<DisplayName>()?.custom = value
    }

val PlayerObject.displayName
    get() = get<DisplayName>()?.name ?: "Unknown name"

object NicknameMixinAccess {
    fun getPlayerDisplayName(player: PlayerObject): Text {
        return player.displayName.formatMm()
    }
}