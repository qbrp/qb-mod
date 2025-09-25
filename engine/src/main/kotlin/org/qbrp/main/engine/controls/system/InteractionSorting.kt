package org.qbrp.main.engine.controls.system

import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject
import org.qbrp.main.engine.controls.entity.interactions.AnyInteraction
import org.qbrp.main.engine.controls.entity.interactions.AvailableInteraction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.system.lookup.InteractionLookup

fun findAvailableInteractions(lookup: InteractionLookup, context: InteractionContext): List<AvailableInteraction> {
    val interactions = lookup.getAnyInteractions().toMutableList()
    val objects = lookup.getInteractableObjects().toMutableList()
    val subjects = lookup.getInteractableSubjects().toMutableList()

    return filterInteractions(objects, subjects, interactions, context)
}

fun groupInteractionsByObjects(interactions: List<AvailableInteraction>): Map<InteractableObject, List<AvailableInteraction>> {
    return interactions.groupBy { it.obj }
}

private fun filterInteractions(
    objects: List<InteractableObject>,
    subjects: List<InteractableSubject>,
    interactions: List<AnyInteraction>,
    context: InteractionContext,
    showAll: Boolean = false
) : List<AvailableInteraction> {
    val availableInteractions = mutableListOf<AvailableInteraction>()
    for (obj in objects) {
        for (subject in subjects) {
            for (interaction in interactions) {
                if (interaction.approach(subject.interactable, obj.interactable)) availableInteractions.add(
                    AvailableInteraction(
                        InteractionId.random(),
                        subject,
                        obj,
                        interaction,
                        context,
                    )
                )
            }
        }
    }
    return availableInteractions
        .filter { (it.show || showAll) }
        .sortedBy { it.interaction.priority }
}