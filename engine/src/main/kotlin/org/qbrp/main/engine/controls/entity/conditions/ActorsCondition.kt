package org.qbrp.main.engine.controls.entity.conditions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import kotlin.reflect.KClass

abstract class ActorsCondition<T: Any>(val actorClass: KClass<T>): GenericCondition {
    abstract fun isActorAvailable(actor: T, ctx: InteractionContext): AvailableResult

    override fun isAvailable(sub: Interactable, obj: Interactable, ctx: InteractionContext): AvailableResult {
        val actors = listOf(sub, obj)
        actors
            .filterIsInstance(actorClass.java)
            .map { isActorAvailable(it, ctx) }
            .none { it is AvailableResult.No }
        return super.isAvailable(sub, obj, ctx)
    }
}