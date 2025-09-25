package org.qbrp.main.engine.controls.system

import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.players.model.ServerPlayerObject

sealed class InteractionCallRequest(val player: ServerPlayerObject) {
    class Context(val context: InteractionRequestContext) : InteractionCallRequest(context.player)
    class Manual(player: ServerPlayerObject, val id: InteractionId) : InteractionCallRequest(player)
}