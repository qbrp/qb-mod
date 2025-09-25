package org.qbrp.main.engine.controls.entity.display.old

import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionName
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.TextSequence

data class InteractionViewData(val id: InteractionId,
                               val subject: InteractableViewData,
                               val obj: InteractableViewData,
                               val name: InteractionName<out TextComponent>,
                               val intent: Intent?,
                               val isAvailable: Boolean,
                               val otherSubjectChildren: List<InteractionViewData>,
                               val otherObjectChildren: List<InteractionViewData>) {
    val chatText: TextComponent get() = TextSequence.Builder()
        .add(subject.name).literal(" : ").add(obj.name).literal(" > ").add(name).build()
}