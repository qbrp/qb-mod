package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class InteractionCallEvent(
    val player: ServerPlayerObject,
    val selection: InteractionSelection,
    val intention: PlayerIntention,
    val context: InteractionRequestContext
) : Event