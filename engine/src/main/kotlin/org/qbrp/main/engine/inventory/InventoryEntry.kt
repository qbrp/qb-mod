package org.qbrp.main.engine.inventory

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.synchronization.SynchronizeConvertible

interface InventoryEntry: Stackable, SynchronizeConvertible, Identifiable {
}