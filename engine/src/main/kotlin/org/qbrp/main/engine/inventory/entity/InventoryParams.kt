package org.qbrp.main.engine.inventory.entity

data class InventoryParams(val maxRows: Int? = null, val maxColumns: Int = 7) {
    companion object {
        val DEFAULT = InventoryParams(null)
        val HORIZONTAL = InventoryParams(1)
    }
}