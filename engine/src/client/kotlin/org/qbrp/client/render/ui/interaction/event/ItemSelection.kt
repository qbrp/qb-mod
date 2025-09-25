package org.qbrp.client.render.ui.interaction.event

import org.qbrp.main.engine.items.model.ItemId

interface ItemSelection {
    val selectedItem: ItemId?
}