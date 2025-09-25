package org.qbrp.client.render.ui.interaction.mode

import org.qbrp.client.engine.interactions.service.ClientInteractionModeManager
import org.qbrp.client.render.ui.UiContext

class InteractionModeViewManager(
    private val clientInteractionModeViewManager: ClientInteractionModeManager,
) {
    private var ticks: Int = 0

    context(ui: UiContext)
    fun flashNoClip() {
        ticks = 0
        val switcher = noClipSwitcher { clientInteractionModeViewManager.noClip }
        ui.viewManager.openView(switcher, ID, override = true, focus = false)
    }

    context(ui: UiContext)
    fun flashInteractionMode() {
        ticks = 0
        val switcher = interactionModeSwitcher { clientInteractionModeViewManager.currentMode }
        ui.viewManager.openView(switcher, ID, override = true, focus = false)
    }

    context(ui: UiContext)
    fun tick() {
        if (ticks++ > 15) {
            ui.viewManager.closeView(ID)
        }
    }

    companion object {
        const val ID = "switcher"
    }
}