package org.qbrp.main.engine.synchronization.impl

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer

class SynchronizerChannelSender(val name: String): Synchronizer {
    override fun sendMessage(
        playerObject: PlayerObject,
        cluster: Cluster
    ) {
        NetworkUtil.sendMessage(playerObject.entity, Message(Messages.syncChannel(name), cluster))
    }
}