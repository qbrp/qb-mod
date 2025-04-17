package org.qbrp.core.game.player.interaction

import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.game.player.interaction.model.BaseInteraction

class InteractionManager(private val session: ServerPlayerSession) {
    companion object {
        val DEFAULT_MODE = InteractionMode(
            "default",
            listOf(BaseInteraction())
        )
    }

    val intent: Intent = Intent.DEFAULT
    val mode: InteractionMode = DEFAULT_MODE

    fun changeInteractionMode(interactionMode: InteractionMode) {

    }
}