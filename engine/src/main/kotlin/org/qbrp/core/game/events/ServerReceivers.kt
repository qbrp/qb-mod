package org.qbrp.core.game.events

import org.qbrp.core.keybinds.ServerKeybinds
import org.qbrp.system.networking.messages.Messages.GET_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.HANDLE_VERSION
import org.qbrp.system.networking.messages.Messages.START_TYPING
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messaging.ServerReceiverContext

// ПЕРЕДЕЛАТЬ!
object ServerReceivers {
    fun register() {
        ServerReceiver<ServerReceiverContext>(GET_CHUNK_VISUAL, StringContent::class, { message, context, receiver ->
            VisualDataStorage.visualDataNetworking.handleGetRequest(message.content as StringContent, receiver as ServerReceiver, context)
        }, { message, context, receiver ->
            //TODO: Сделать перезагрузку
        }
        ).register()
        ServerReceiver<ServerReceiverContext>(HANDLE_VERSION, StringContent::class, { message, context, receiver ->
            VersionChecker.handlePlayer(context.player, message.getContent())
            true
        }).register()
        ServerKeybinds.registerKeybindReceiver("spectators_spawn")
    }

}