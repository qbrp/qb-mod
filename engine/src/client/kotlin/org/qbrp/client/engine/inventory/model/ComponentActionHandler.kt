package org.qbrp.client.engine.inventory.model

import org.qbrp.client.engine.inventory.ActionMessageSender
import org.qbrp.main.core.game.model.components.Component

class ComponentActionHandler(val actionMessageSender: ActionMessageSender): Component(), ActionHandler {
    override fun takeItem(slot: Int, containerId: String) {
        actionMessageSender.sendTakeItemMessage(slot, containerId, requireState().obj.id)
    }

    override fun putItem(slot: Int, containerId: String) {
        actionMessageSender.sendPutItemMessage(slot, containerId, requireState().obj.id)
    }

    override fun swapItem(slot: Int, containerId: String) {
        actionMessageSender.sendSwapItemMessage(slot, containerId, requireState().obj.id)
    }
}