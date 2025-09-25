package org.qbrp.main.engine.controls.system

import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface InteractionSystem {
    fun requestCall(request: InteractionCallRequest)
    fun requestList(request: InteractionListRequest)
    fun toggleAlternateMode(player: ServerPlayerObject)
    fun setIntent(player: ServerPlayerObject, intent: Intent)
    fun setInteractionMode(player: ServerPlayerObject, mode: InteractionMode)
    fun setNoClip(player: ServerPlayerObject, value: Boolean)
}