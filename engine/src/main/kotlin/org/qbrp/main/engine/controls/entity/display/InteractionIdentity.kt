package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.entity.interactions.InteractionKind

data class InteractionIdentity(val id: InteractionId, val kind: InteractionKind, val availability: InteractionAvailability)

sealed class InteractionAvailability() {
    object Yes : InteractionAvailability()
    class No(val reasons: List<String>) : InteractionAvailability()
}