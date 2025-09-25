package org.qbrp.main.engine.controls.entity.conditions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import kotlin.reflect.KClass

abstract class SymmetricalCondition<Actor1: Interactable, Actor2: Interactable>(val firstActor: KClass<Actor1>,
                                                                                val secondActor: KClass<Actor2>): GenericCondition {
    abstract fun isActorsAvailable(actor1: Actor1, actor2: Actor2, ctx: InteractionContext): AvailableResult

    override fun isAvailable(sub: Interactable, obj: Interactable, ctx: InteractionContext): AvailableResult {
        val actors = listOf(sub, obj)

        val firstActorInstance = actors.filterIsInstance(firstActor.java).firstOrNull()
        val secondActorInstance = actors.filterIsInstance(secondActor.java).firstOrNull()

        return if (firstActorInstance != null && secondActorInstance != null) {
            isActorsAvailable(firstActorInstance, secondActorInstance, ctx)
        } else {
            AvailableResult.No("")
        }
    }
}