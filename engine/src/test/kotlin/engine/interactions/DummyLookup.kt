package engine.interactions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject
import org.qbrp.main.engine.controls.system.asObject
import org.qbrp.main.engine.controls.system.asSubject
import org.qbrp.main.engine.controls.entity.interactions.AnyInteraction
import org.qbrp.main.engine.controls.system.lookup.InteractionLookup

class DummyLookup(private val interactions: List<AnyInteraction>,
                  private val objects: List<Interactable>,
                  private val subjects: List<Interactable>): InteractionLookup {
    override fun getAnyInteractions(): List<AnyInteraction> {
        return interactions
    }

    override fun getInteractableObjects(): List<InteractableObject> {
        return objects.map { it.asObject() }
    }

    override fun getInteractableSubjects(): List<InteractableSubject> {
        return subjects.map { it.asSubject() }
    }
}