package org.qbrp.main.engine.controls.model.inspect

import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.controls.dsl.finish
import org.qbrp.main.engine.controls.dsl.interaction
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.InteractionPriorities
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.model.player.CharacterInteractable
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.model.player.condition.OtherActionCondition

object InspectionInteractions {
    private val INSPECTION = interaction<PlayerInteractable, Interactable> {
        identity { subject(InteractableTypes.PLAYER) }
        priority(InteractionPriorities.LOWEST)
    }

    val CHARACTER = interaction<PlayerInteractable, CharacterInteractable> {
        identity {
            obj { InteractableTypes.CHARACTER }
            kind("inspect_character")
        }

        condition(OtherActionCondition())
        execute { inspector, character, ctx ->
            val description = character.view.appearance
            val text = description
                .ifEmpty { "Описание не найдено" }
                .formatMm()
            val name = character.view.name
            val message = "$name &d( &r$text&d )&r"
            inspector.sendChatMessage(message)
        }

        extends(INSPECTION)
    }.finish()
}