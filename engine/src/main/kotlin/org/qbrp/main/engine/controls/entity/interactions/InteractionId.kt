package org.qbrp.main.engine.controls.entity.interactions

import org.qbrp.main.core.game.IDGenerator

@JvmInline
value class InteractionId(val value: Long) {
    companion object {
        fun random() = InteractionId(IDGenerator.nextId())
    }
}

@JvmInline
value class InteractionKind(val name: String)

fun Long.toInteractionId() = InteractionId(this)