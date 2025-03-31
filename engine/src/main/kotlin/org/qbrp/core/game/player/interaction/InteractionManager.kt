package org.qbrp.core.game.player.interaction

import org.qbrp.core.game.player.ServerPlayerSession

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

    init {
        //InteractEvents.getOrCreate(Interactions.BASE).register { plr, intent ->
           // intent
        //}
    }
}