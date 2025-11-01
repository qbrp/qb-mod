package org.qbrp.client.controls

import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.entity.interactions.InteractionId

class ClientInteractionManager {
    private var activation: InteractionContextMenuViewData? = null
    var intent: Intent = Intent.DEFAULT
    var alternate: Boolean = false

    fun applyInteractActivation(activation: InteractionContextMenuViewData) {
        this.activation = activation
        InteractionContextMenuApplyEvent(activation).emit()
    }

    fun call() {
        ClientInteractionCallEvent(
            getSelectedInventory(),
            getSelectedItem()
        ).emit()
    }

    fun invokeInteraction(id: InteractionId): Boolean {
        val activation = activation
        if (activation == null) return false
        SelectInteractionEvent(id).emit()
        return true
    }
}