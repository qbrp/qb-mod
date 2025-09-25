package org.qbrp.client.engine.interactions.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData

data class InteractionContextMenuApplyEvent(val contextMenuViewData: InteractionContextMenuViewData): Event