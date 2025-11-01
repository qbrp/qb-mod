package org.qbrp.client.render

import config.ClientConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.client.controls.ClientInteractionModeManager
import org.qbrp.main.engine.controls.entity.InteractionMode

object ActionBarMixinRenderData : KoinComponent {
    private val interactionModeManager: ClientInteractionModeManager by inject()
    private val interactionMode get() = interactionModeManager.currentMode

    val RENDER_ACTION_BAR get() = interactionMode == InteractionMode.BUILDER || interactionMode == InteractionMode.CROW
    val OVERRIDE_HAND_ITEMS get() = interactionMode != InteractionMode.BUILDER
    val RENDER_SURVIVAL_BAR get() = ClientConfig.renderVanillaHud
}