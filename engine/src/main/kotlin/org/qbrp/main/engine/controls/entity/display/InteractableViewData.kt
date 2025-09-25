package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.TextComponent

data class InteractableViewData(val name: InteractableName<out TextComponent>, val icon: InteractionIcon) {
    companion object {
        fun of(name: TextComponent, icon: InteractionIcon): InteractableViewData {
            return InteractableViewData(InteractableName(name), icon)
        }

        fun of(literal: String, iconId: IconTextureId): InteractableViewData {
            return of(LiteralTextComponent(literal), InteractionIcon.Asset(iconId))
        }
    }
}