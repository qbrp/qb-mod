package org.qbrp.main.engine.controls.entity.interactions

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.display.InteractionAvailability

interface Interaction<Subject: Interactable, Object: Interactable> {
    val obj: InteractableType<Object>
    val subject: InteractableType<Subject>
    val priority: Int
    val conditions: List<Condition<Subject, Object>>
    val executors: List<Executor<Subject, Object>>
    val kind: InteractionKind

    fun execute(subject: Subject, obj: Object, ctx: InteractionContext) {
        executors.forEach { it.execute(subject, obj, ctx) }
    }

    fun approach(subject: Interactable, obj: Interactable): Boolean {
        return this.subject == subject.interactableType && this.obj == obj.interactableType
    }

    fun shouldShow(subject: Subject, obj: Object, ctx: InteractionContext): Boolean {
        return !conditions.any { !it.show(subject, obj, ctx) }
    }

    fun isAvailable(subject: Subject, obj: Object, ctx: InteractionContext): InteractionAvailability {
        val results = conditions.map { it.isAvailable(subject, obj, ctx) }
        val failures = results.filterIsInstance<AvailableResult.No>()

        return if (failures.isNotEmpty()) InteractionAvailability.No(failures.map { it.message }) else InteractionAvailability.Yes
    }
}

typealias AnyInteraction = Interaction<*, *>