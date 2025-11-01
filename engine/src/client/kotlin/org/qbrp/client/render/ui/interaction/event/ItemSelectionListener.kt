package org.qbrp.client.render.ui.interaction.event

import org.qbrp.client.render.ui.view.ScreenEventHandler
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.event.UiEventListener
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.EventType

class ItemSelectionListener<V : ViewComponent>(
    eventType: EventType<InventorySelectionCallback, Collection<ItemSelection>>,
    handler: ScreenEventHandler<V, InventorySelectionCallback, ItemSelection>
) : UiEventListener<V, InventorySelectionCallback, ItemSelection>(
    eventType,
    5,
    handler
)
