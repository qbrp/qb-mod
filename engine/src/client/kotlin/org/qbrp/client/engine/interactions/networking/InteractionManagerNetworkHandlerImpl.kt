package org.qbrp.client.engine.interactions.networking

import org.qbrp.client.engine.interactions.service.ClientInteractionManager
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.dto.InteractionContextMenuViewDto
import org.qbrp.main.engine.controls.dto.InteractionViewMapper

class InteractionManagerNetworkHandlerImpl(
    private val interactionManager: ClientInteractionManager,
    private val viewMapper: InteractionViewMapper,
    private val eventContext: EventContext
) : InteractionManagerNetworkHandler {
    override fun onSync(intent: Intent, alternate: Boolean) {
        interactionManager.intent = intent
        interactionManager.alternate = alternate
    }

    override fun onInteractionList(renderData: InteractionContextMenuViewDto) = with(eventContext) {
        val viewData = viewMapper.contextMenuDtoToView(renderData)
        interactionManager.applyInteractActivation(viewData)
    }
}