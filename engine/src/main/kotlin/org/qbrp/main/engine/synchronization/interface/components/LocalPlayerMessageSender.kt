package org.qbrp.main.engine.synchronization.`interface`.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.LocalPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.types.SendContent

interface LocalPlayerMessageSender {
    val messageSender: ObjectMessageSender
    fun sendMessage(content: Cluster, player: LocalPlayerObject, id: String) {
        messageSender.sendMessage(content, id, this as BaseObject, player)
    }
}