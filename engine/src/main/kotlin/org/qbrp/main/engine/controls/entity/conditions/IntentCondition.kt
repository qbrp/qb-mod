package org.qbrp.main.engine.controls.entity.conditions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

class IntentCondition(val intent: Intent): GenericCondition {
    override fun show(
        sub: Interactable,
        obj: Interactable,
        ctx: InteractionContext
    ): Boolean {
        return intent == (ctx.intention.intent ?: return true)
    }
}