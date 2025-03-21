package org.qbrp.engine.client.system

import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.system.modules.ModuleManager
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.BooleanContent

class ClientModuleManager: ModuleManager() {

    fun registerStateReceivers() {
        modules.forEach {
            ClientReceiver<ClientReceiverContext>(Messages.moduleUpdate(it.getName()), BooleanContent::class) { message, context, receiver ->
                it.serverState = message.getContent()
                true
            }.register()
        }
    }

}