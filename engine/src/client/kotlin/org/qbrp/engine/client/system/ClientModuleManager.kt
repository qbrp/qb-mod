package org.qbrp.engine.client.system

import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.system.modules.ModuleManager
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.BooleanContent

object ClientModuleManager: ModuleManager() {
    fun registerStateReceivers() {
//        modules.forEach {
//            ClientReceiver<ClientReceiverContext>(Messages.moduleUpdate(it.name), BooleanContent::class) { message, context, receiver ->
//                if (message.getContent<Boolean>()) it.enable() else it.disable()
//                true
//            }.register()
//        }
        TODO()
    }

}