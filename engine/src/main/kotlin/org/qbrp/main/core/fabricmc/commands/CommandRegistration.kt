package org.qbrp.main.core.fabricmc.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.Koin
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.fabricmc.registerDamageControlCommand
import org.qbrp.main.core.regions.registerRegionCommands
import org.qbrp.main.engine.assets.contentpacks.registerContentPacksCommand
import org.qbrp.main.engine.chat.addons.registerPrivateMessageCommand
import org.qbrp.main.engine.chat.addons.registerRpCommands
import org.qbrp.main.engine.players.attributes.registerPlayerAttributesCommand
import org.qbrp.main.engine.players.characters.registerCharacterCommands
import org.qbrp.main.engine.players.registerNicknameCommand
import org.qbrp.main.engine.spectators.registerSpectatorCommands
import org.qbrp.main.engine.time.registerTimeCommands

typealias ServerCommandDispatcher = CommandDispatcher<ServerCommandSource>

fun ServerCommandDispatcher.registerServerCommands(koin: Koin) {
    registerDamageControlCommand()
    registerRegionCommands(koin.get(), koin.get())
    registerContentPacksCommand(koin.get())
    registerRpCommands(koin.get<ServerConfigData>().chat.commands)
    registerPrivateMessageCommand(koin.get())
    registerPlayerAttributesCommand()
    registerCharacterCommands()
    registerNicknameCommand()
    registerSpectatorCommands()
    registerTimeCommands(koin.get())
}