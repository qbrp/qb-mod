package org.qbrp.main.engine.items.components.containers

import org.qbrp.main.engine.inventory.InventoryEntry
import org.qbrp.main.core.synchronization.SynchronizeConvertible

interface VolumeContainable: SynchronizeConvertible, InventoryEntry {
    val weightGrams: Int
    val dimensions: Dimensions
    val volume: Double
}