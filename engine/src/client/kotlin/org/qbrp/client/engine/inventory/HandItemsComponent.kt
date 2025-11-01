package org.qbrp.client.engine.inventory

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.players.model.PlayerComponent
import org.qbrp.main.engine.players.model.PlayerComponentInstantiateContext

class HandItemsComponent(
    ctx: PlayerComponentInstantiateContext,
    val rightHand: ClientContainerState,
    val leftHand: ClientContainerState
): PlayerComponent(ctx) {
    fun getLeftHandDisplay(): ItemDisplay? {
        return leftHand.items.firstOrNull()?.get()
    }
    fun getRightHandDisplay(): ItemDisplay? {
        return rightHand.items.firstOrNull()?.get()
    }
}