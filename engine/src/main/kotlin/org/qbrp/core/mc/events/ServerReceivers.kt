package org.qbrp.core.mc.events

import org.qbrp.core.keybinds.ServerKeybinds
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.messages.Messages.HANDLE_VERSION
import org.qbrp.system.networking.messages.Messages.AUTH
import org.qbrp.system.networking.messaging.ServerReceiverContext


object ServerReceivers {
    fun register() {
        ServerReceiver<ServerReceiverContext>(HANDLE_VERSION, StringContent::class, { message, context, receiver ->
            VersionChecker.handlePlayer(context.player, message.getContent())
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(AUTH, StringContent::class, { message, context, receiver ->
            PlayerManager.lifecycleManager.handleConnected(context.player, message.getContent())
            true
        }).register()
        ServerKeybinds.registerKeybindReceiver("spectators_spawn")
    }

}