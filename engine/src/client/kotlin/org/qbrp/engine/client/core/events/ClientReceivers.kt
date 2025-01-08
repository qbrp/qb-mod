package org.qbrp.engine.client.core.events

import org.qbrp.engine.client.visual.VisualDataLoader
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.JsonArrayContent
import org.qbrp.system.networking.messages.Messages.LOAD_CHUNK_VISUAL

object ClientReceivers {

    fun register() {
        ClientReceiver(LOAD_CHUNK_VISUAL, JsonArrayContent::class) { message, context ->
            VisualDataLoader.loadChunk((message.content as JsonArrayContent).array)
        }.register()
    }
}