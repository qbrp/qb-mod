package org.qbrp.core.game.blocks

import net.minecraft.block.Block
import net.minecraft.block.Blocks

class ServerBlocks {
    private val blockOverrides = mutableMapOf<Block, ServerBlock>()

    init {
        override(Blocks.OAK_LOG, CustomTextureBlock("oak_log"))
    }

    fun override(block: Block, template: ServerBlock)  {
        blockOverrides[block] = template
    }

}