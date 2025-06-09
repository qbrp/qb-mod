package org.qbrp.main.core.synchronization.impl

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.synchronization.Synchronizer

class SynchronizerChannelSender(val name: String): Synchronizer {
    override fun sendMessage(
        playerObject: ServerPlayerObject,
        cluster: Cluster
    ) {
        NetworkUtil.sendMessage(Message(Messages.syncChannel(name), cluster), playerObject.entity)
    }
}