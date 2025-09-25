package org.qbrp.main.engine.controls.entity.conditions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

interface Condition<in Sub: Interactable, in Obj: Interactable> {
    fun show(sub: Sub, obj: Obj, ctx: InteractionContext): Boolean { return isAvailable(sub, obj, ctx) is AvailableResult.Yes }
    fun isAvailable(sub: Sub, obj: Obj, ctx: InteractionContext): AvailableResult { return AvailableResult.Yes }
}