package org.qbrp.main.engine.controls.entity.interactions

import org.qbrp.main.engine.controls.entity.interactable.Interactable

fun interface Executor<in Subject: Interactable, in Object: Interactable> {
    fun execute(subject: Subject, obj: Object, ctx: InteractionContext)
}