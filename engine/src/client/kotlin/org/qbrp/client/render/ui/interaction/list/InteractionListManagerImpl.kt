package org.qbrp.client.render.ui.interaction.list

import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.client.render.ui.interaction.InteractionsViewModule.Companion.VIEW_ID
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.tooltip.TooltipColorProfile

class InteractionListManagerImpl(
    private val interactionListFactory: InteractionListFactory,
    private val viewManager: ViewManager,
    private val interactionListColorProfile: TooltipColorProfile
) : InteractionListManager {
    private val closeListener = InteractionSelectionCloseHandler()
    override val isOpen: Boolean
        get() = interactionList != null
    override var interactionList: AppliedInteractionList? = null
        private set

    context(ui: UiContext)
    fun openInteractionList(contextMenuViewData: InteractionContextMenuViewData) = with(ui) {
        interactionList?.let { viewManager.closeView(it.view) }
        val newSelection = interactionListFactory.create(contextMenuViewData, closeListener, interactionListColorProfile)
        interactionList = newSelection
        viewManager.openView(newSelection.view, VIEW_ID)
        newSelection.view.bringToFront()
        tooltipManager.hideTooltip()
        mouse.unlock()
    }

    inner class InteractionSelectionCloseHandler() : CloseListener {
        override fun onViewClosed(view: View) {
            viewManager.closeView(VIEW_ID)
            interactionList = null
        }
    }
}