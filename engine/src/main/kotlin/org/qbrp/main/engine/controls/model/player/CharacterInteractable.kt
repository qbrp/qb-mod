package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.text.components.TextComponent

interface CharacterInteractable : Interactable, IdentifiableInteractable {
    val view: View
    val carrier: ItemCarrierInteractable

    interface View {
        val name: String
        val appearance: String
    }
}