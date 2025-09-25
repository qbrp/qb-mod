package org.qbrp.main.engine.controls.model.container.condition

import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.container.LocateableInteractable

class ItemInDistanceCondition(val distance: Double = 4.0): Condition<LocateableInteractable, LocateableInteractable> {
    override fun isAvailable(sub: LocateableInteractable, obj: LocateableInteractable, ctx: InteractionContext): AvailableResult {
        return if ((sub.pos.squaredDistanceTo(obj.pos) < distance && sub.world == obj.world) || sub.ignorePosCondition || obj.ignorePosCondition)
            AvailableResult.Yes
        else
            AvailableResult.No("Предмет находится вне зоны досягаемости")
    }
}