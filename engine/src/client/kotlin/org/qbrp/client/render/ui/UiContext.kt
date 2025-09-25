package org.qbrp.client.render.ui

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.tooltip.render.TooltipBackgroundRenderer
import org.qbrp.client.render.ui.components.tooltip.TooltipManager
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.main.core.components.ComponentRegistry

class UiContext(
    val screen: Screen,
    val componentRegistry: ComponentRegistry,
    val textRenderer: TextRenderer,
    val mouse: MouseState,
    val tooltipBackgroundRenderer: TooltipBackgroundRenderer,
    val playerProfileManager: PlayerProfileManager,
    val viewManager: ViewManager,
    val tooltipManager: TooltipManager
): ComponentRegistry by componentRegistry

fun <T> KoinComponent.withUiContext(block: context(UiContext) () -> T): T {
    with(get<UiContext>()) {
        return block()
    }
}