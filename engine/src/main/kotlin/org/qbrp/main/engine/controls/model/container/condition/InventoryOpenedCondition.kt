package org.qbrp.main.engine.controls.model.container.condition

import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactable.Interactable

class InventoryOpenedCondition : Condition<Interactable, Interactable> {
    override fun isAvailable(
        sub: Interactable,
        obj: Interactable,
        ctx: InteractionContext
    ): AvailableResult {
        return if (ctx.call.inventoryOpened) {
            AvailableResult.Yes
        } else {
            AvailableResult.No("Инвентарь не открыт")
        }
    }
}