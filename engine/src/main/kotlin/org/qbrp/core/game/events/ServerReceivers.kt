package org.qbrp.core.game.events

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.PlayerManager.getPlayerSession
import org.qbrp.core.keybinds.ServerKeybinds
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.HANDLE_VERSION
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.networking.messaging.ServerReceiverContext

fun registerServerReceivers() {
    ServerReceiver<ServerReceiverContext>(HANDLE_VERSION, StringContent::class, { message, context, receiver ->
        VersionChecker.handlePlayer(context.player, message.getContent())
        true
    }).register()
    ServerReceiver<ServerReceiverContext>(Messages.INTERACTION_PLAYER, StringContent::class, { message, context, receiver ->
            val session = PlayerManager.getPlayerSession(message.getContent<String>()) ?: return@ServerReceiver true
            NetworkManager.sendMessage(
                context.player,
                Message(
                    Messages.NICKNAME_SHOW,
                    ClusterBuilder()
                        .component("name", session.entity.name.string)
                        .component("rp", session.getDisplayName())
                        .component("template", ServerResources.getConfig().chat.nicknameMessage)
                        .build()
                )
            )
            true
        }).register()
    ServerKeybinds.registerKeybindReceiver("spectators_spawn")
    ServerKeybinds.registerKeybindReceiver("hand_to_hand")
}