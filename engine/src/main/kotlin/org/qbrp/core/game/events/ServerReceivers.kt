package org.qbrp.core.game.events

import org.qbrp.system.networking.messages.Messages.GET_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.Engine
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.START_TYPING
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messaging.ServerReceiverContext

object ServerReceivers {
    fun register() {
        ServerReceiver<ServerReceiverContext>(GET_CHUNK_VISUAL, StringContent::class, { message, context, receiver ->
            VisualDataStorage.visualDataNetworking.handleGetRequest(message.content as StringContent, receiver as ServerReceiver, context)
        }, { message, context, receiver ->
            //TODO: Сделать перезагрузку
        }
        ).register()
        ServerReceiver<ServerReceiverContext>(START_TYPING, Signal::class, { message, context, receiver ->
            Engine.chatModule.API.playerStartTyping(context.player)
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(END_TYPING, Signal::class, { message, context, receiver ->
            Engine.chatModule.API.playerEndTyping(context.player)
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(SEND_MESSAGE, Cluster::class, { message, context, receiver ->
            Engine.chatModule.API.handleMessagePacket(message)
            true
        }).register()
    }

}