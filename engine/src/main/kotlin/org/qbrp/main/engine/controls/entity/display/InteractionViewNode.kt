package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.mechanics.tooltip.component.TooltipComponent

data class InteractionViewNode(
    val interaction: InteractionIdentity?,
    val label: TextComponent,
    val icon: InteractionIcon? = null,
    val description: List<TooltipComponent> = listOf(),
    val children: List<InteractionViewNode> = listOf()
)

fun EmptyNode(label: TextComponent, icon: InteractionIcon? = null, description: List<TooltipComponent> = listOf(), children: List<InteractionViewNode> = listOf()) =
    InteractionViewNode(null, label, icon, description, children)
