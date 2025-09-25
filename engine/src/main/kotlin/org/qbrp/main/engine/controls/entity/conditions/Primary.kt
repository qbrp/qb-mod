package org.qbrp.main.engine.controls.entity.conditions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

class Primary: GenericCondition {
    override fun show(
        sub: Interactable,
        obj: Interactable,
        ctx: InteractionContext
    ): Boolean {
        return !ctx.intention.alternate
    }
}