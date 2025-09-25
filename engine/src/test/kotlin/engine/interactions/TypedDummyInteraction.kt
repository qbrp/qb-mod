package engine.interactions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.display.InteractionName
import org.qbrp.main.engine.controls.entity.interactions.Executor
import org.qbrp.main.engine.controls.entity.interactions.InteractionDisplay
import org.qbrp.main.engine.controls.entity.interactions.Interaction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

class TypedDummyInteraction<Sub: Interactable, Obj: Interactable>(
    val name: String,
    override val obj: InteractableType<Obj>,
    override val subject: InteractableType<Sub>,
    override val executors: List<Executor<in Sub, in Obj>>,
    val shouldShow: Boolean? = null,
    val isAvailable: Boolean? = null,
    override val priority: Int = 1,
    override val conditions: List<Condition<Sub, Obj>> = listOf(),
    val throwsException: Boolean = false,
    override val subinteractions: List<Interaction<*, *>> = listOf()
) : Interaction<Sub, Obj> {
    override fun getDisplay(subject: Sub, obj: Obj, ctx: InteractionContext): InteractionDisplay {
        return InteractionDisplay(InteractionName.literal(name))
    }

    override fun shouldShow(subject: Sub, obj: Obj, ctx: InteractionContext) = shouldShow ?: super.shouldShow(subject, obj, ctx)
    override fun isAvailable(subject: Sub, obj: Obj, ctx: InteractionContext) = isAvailable ?: super.isAvailable(subject, obj, ctx)

    override fun execute(subject: Sub, obj: Obj, ctx: InteractionContext) {
        if (throwsException) throw RuntimeException("Test exception")
    }
}