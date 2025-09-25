package org.qbrp.main.engine.controls.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface ServerInteractionListener: PacketListener {
    fun onListRequest(player: ServerPlayerObject, call: InteractionRequestContext)
    fun onSelectInteraction(player: ServerPlayerObject, id: InteractionId)
    fun onInteractionMode(player: ServerPlayerObject, mode: InteractionMode)
    fun onNoClip(player: ServerPlayerObject, enabled: Boolean)
}