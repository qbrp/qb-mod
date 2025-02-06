package org.qbrp.core.game

import org.qbrp.core.game.blocks.ServerBlocks
import org.qbrp.core.game.items.BaseItem
import org.qbrp.core.game.items.Items
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