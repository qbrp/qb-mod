package org.qbrp.client.engine.inventory.model

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.engine.inventory.Stackable

interface ClientInventoryEntry: Stackable, Identifiable {
}