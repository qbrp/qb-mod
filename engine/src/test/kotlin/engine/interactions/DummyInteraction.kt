package engine.interactions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.display.InteractionName
import org.qbrp.main.engine.controls.entity.interactions.Executor
import org.qbrp.main.engine.controls.entity.interactions.InteractionDisplay
import org.qbrp.main.engine.controls.entity.interactions.Interaction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

class DummyInteraction(
    val name: String,
    override val obj: InteractableType<Interactable>,
    override val subject: InteractableType<Interactable>,
    override val executors: List<Executor<Interactable, Interactable>>,
    val shouldShow: Boolean? = null,
    val isAvailable: Boolean? = null,
    override val priority: Int = 1,
    override val conditions: List<Condition<Interactable, Interactable>> = listOf(),
    val throwsException: Boolean = false,
    override val subinteractions: List<Interaction<*, *>> = listOf(),
) : Interaction<Interactable, Interactable> {
    override fun getDisplay(subject: Interactable, obj: Interactable, ctx: InteractionContext): InteractionDisplay {
        return InteractionDisplay(InteractionName.literal(name))
    }
    override fun shouldShow(subject: Interactable, obj: Interactable, context: InteractionContext) = shouldShow ?: super.shouldShow(subject, obj, context)
    override fun isAvailable(subject: Interactable, obj: Interactable, context: InteractionContext) = isAvailable ?: super.isAvailable(subject, obj, context)

    override fun execute(subject: Interactable, obj: Interactable, context: InteractionContext) {
        if (throwsException) throw RuntimeException("Test exception")
    }
}