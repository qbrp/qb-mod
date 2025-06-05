package org.qbrp.main.engine.synchronization.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface ObjectMessageSender {
    fun sendMessage(content: Cluster, id: String, obj: BaseObject, player: PlayerObject)
}