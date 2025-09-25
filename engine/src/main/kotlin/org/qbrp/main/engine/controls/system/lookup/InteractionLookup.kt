package org.qbrp.main.engine.controls.system.lookup

import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject
import org.qbrp.main.engine.controls.entity.interactions.AnyInteraction

interface InteractionLookup {
    fun getAnyInteractions(): List<AnyInteraction>
    fun getInteractableObjects(): List<InteractableObject> = emptyList()
    fun getInteractableSubjects(): List<InteractableSubject> = emptyList()
}