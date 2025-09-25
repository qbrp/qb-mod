package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.players.characters.components.CharactersBank

class CharacterAppliedCondition: Condition<Interactable, Interactable> {
    override fun isAvailable(sub: Interactable, obj: Interactable, ctx: InteractionContext): AvailableResult {
        return when(ctx.player.get<CharactersBank>()?.character != null) {
            false -> AvailableResult.No("Персонаж не применён")
            true -> AvailableResult.Yes
        }
    }
}