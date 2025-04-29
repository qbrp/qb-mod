package org.qbrp.core.mc

import org.qbrp.core.mc.blocks.ServerBlocks
import org.qbrp.core.mc.items.BaseItem
import org.qbrp.core.mc.items.Items
import org.qbrp.core.resources.structure.integrated.Parents

object Game {
    val items = Items()
    val serverBlocks = ServerBlocks()

    fun init() {
        items.registerItems(listOf(
            BaseItem("custom_item_generated", modelType = Parents.GENERATED),
            BaseItem("custom_item_handheld", modelType = Parents.HANDHELD)
        ))
    }
}