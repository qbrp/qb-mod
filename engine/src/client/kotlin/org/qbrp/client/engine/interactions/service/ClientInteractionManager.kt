package org.qbrp.client.engine.interactions.service

import org.qbrp.client.engine.interactions.event.InteractionContextMenuApplyEvent
import org.qbrp.client.engine.interactions.networking.InteractionPacketSender
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.entity.interactions.InteractionId

class ClientInteractionManager(
    private val packetSender: InteractionPacketSender,
    private val selectionCollector: SelectionCollector
) {
    private var activation: InteractionContextMenuViewData? = null
    var intent: Intent = Intent.DEFAULT
    var alternate: Boolean = false

    context(event: EventContext)
    fun applyInteractActivation(activation: InteractionContextMenuViewData) {
        this.activation = activation
        InteractionContextMenuApplyEvent(activation).emit()
    }

    fun call() {
        packetSender.sendInteractionCallPacket(
            selectionCollector.getSelectedInventory(),
            selectionCollector.getSelectedItem()
        )
    }

    fun invokeInteraction(id: InteractionId): Boolean {
        val activation = activation
        if (activation == null) return false
        packetSender.sendInvokeInteractionPacket(id)
        return true
    }
}