package org.qbrp.client.engine.players

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.synchronization.components.C2SMessaging
import org.qbrp.main.core.synchronization.components.MessagingChannelSender

class ClientPlayerObject(override val entity: PlayerEntity?, override val entityName: String,
                         override val messageSender: MessagingChannelSender
): PlayerObject(), C2SMessaging {
}