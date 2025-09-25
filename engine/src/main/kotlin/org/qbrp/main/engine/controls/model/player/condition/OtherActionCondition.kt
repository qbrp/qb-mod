package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.conditions.GenericCondition
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.player.IdentifiableInteractable
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.players.model.ServerPlayerObject

class OtherActionCondition(): Condition<IdentifiableInteractable, IdentifiableInteractable> {
    override fun isAvailable(sub: IdentifiableInteractable, obj: IdentifiableInteractable, ctx: InteractionContext): AvailableResult {
        val isOther = sub.id != obj.id
        return if (isOther) AvailableResult.Yes else AvailableResult.No("Действие можно применить по отношению только к другому игроку")
    }
}