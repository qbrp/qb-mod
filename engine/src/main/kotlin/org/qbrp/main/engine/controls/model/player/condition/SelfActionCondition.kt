package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

class SelfActionCondition: Condition<PlayerInteractable, PlayerInteractable> {
    override fun isAvailable(sub: PlayerInteractable, obj: PlayerInteractable, ctx: InteractionContext): AvailableResult {
        val self = sub.session.playerId == obj.session.playerId
        return when(self) {
            true -> AvailableResult.Yes
            false -> AvailableResult.No("Действия можно применить только по отношению к самому себе")
        }
    }
}