package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.props.components.PropManager.Companion.propSpawnAvailable

class CanSpawnedAsPropCondition: Condition<Interactable, ItemInteractable> {
    override fun isAvailable(sub: Interactable, obj: ItemInteractable, ctx: InteractionContext): AvailableResult {
        return if (obj.propSpawnAvailable()) AvailableResult.Yes else AvailableResult.No("редмет нельзя использовать как проп")
    }
}