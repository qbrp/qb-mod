package org.qbrp.client.core

import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.BooleanContent

class ClientModule(name: String): QbModule(name) {
    fun registerStateReceiver() {
        ClientReceiver(Messages.moduleUpdate(getName()), BooleanContent::class) { message, context, receiver ->
            serverState = message.getContent()
            true
        }.register()
    }
}