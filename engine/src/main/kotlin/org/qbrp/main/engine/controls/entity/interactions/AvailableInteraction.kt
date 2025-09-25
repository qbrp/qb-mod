package org.qbrp.main.engine.controls.entity.interactions

import org.qbrp.main.engine.controls.entity.display.InteractionAvailability
import org.qbrp.main.engine.controls.entity.display.InteractionIdentity
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject

class AvailableInteraction(
    val id: InteractionId,
    val subject: InteractableSubject,
    val obj: InteractableObject,
    val interaction: AnyInteraction,
    val context: InteractionContext
) {
    @Suppress("UNCHECKED_CAST")
    private val castedInteraction get() = interaction as Interaction<Interactable, Interactable>

    val show: Boolean get() = castedInteraction.shouldShow(subject.interactable, obj.interactable, context)

    val availability: InteractionAvailability get() = castedInteraction.isAvailable(subject.interactable, obj.interactable, context)

    val isAvailable get() = availability == InteractionAvailability.Yes

    val identity = InteractionIdentity(id, interaction.kind, availability)

    fun execute() {
        castedInteraction.execute(subject.interactable, obj.interactable, context)
    }
}