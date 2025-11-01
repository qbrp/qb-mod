package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.util.StringId
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.text.miniMessage
import org.qbrp.main.engine.players.characters.components.LookManager

class CharacterInteractableImpl(private val player: ServerPlayerObject, override val carrier: ItemCarrierInteractable,): CharacterInteractable {
    private val formattedName: String
        get() = player.require<LookManager>().getFormattedName()
    override val interactableType: AnyInteractableType
        get() = InteractableTypes.CHARACTER
    override val id: StringId
        get() = player.stringId
    override val interactableDisplay: InteractableViewData get() = InteractableViewData.of(
        miniMessage(formattedName),
        InteractionIcon.Player(player.playerId),
    )
    override val view: CharacterInteractable.View get() = View()

    inner class View: CharacterInteractable.View {
        override val name: String
            get() = formattedName
        override val appearance: String
            get() = player.require<LookManager>().getDescriptionOrDefault()
    }
}