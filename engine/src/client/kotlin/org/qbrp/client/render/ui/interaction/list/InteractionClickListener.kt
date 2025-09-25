package org.qbrp.client.render.ui.interaction.list

import org.lwjgl.glfw.GLFW
import org.qbrp.client.engine.interactions.service.ClientInteractionManager
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.dataview.PopupMenuItem
import org.qbrp.client.render.ui.components.dataview.PopupMenuItemClickListener
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.engine.controls.entity.display.old.InteractionViewData
import org.qbrp.main.engine.controls.entity.interactions.InteractionId

typealias SubInteractionViewBuilder = context(UiContext) (List<InteractionViewData>, InteractionClickListener) -> View

class InteractionClickListener(
    val id: InteractionId,
    private val interactionManager: ClientInteractionManager
) : PopupMenuItemClickListener {
    override fun onMenuItemClicked(button: Int, pos: Vec2, item: PopupMenuItem): Boolean {
        return if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            interactionManager.invokeInteraction(id)
            true
        } else {
            false
        }
    }
}