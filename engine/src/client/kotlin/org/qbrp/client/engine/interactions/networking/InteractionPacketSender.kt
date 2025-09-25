package org.qbrp.client.engine.interactions.networking

import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.InteractionId

interface InteractionPacketSender {
    fun sendInteractionCallPacket(selectedInventory: ContainerId?, selectedItem: ClientItemObject?)
    fun sendInvokeInteractionPacket(id: InteractionId)
    fun sendInteractionModePacket(mode: InteractionMode)
    fun sendNoClipPacket(enabled: Boolean)
}