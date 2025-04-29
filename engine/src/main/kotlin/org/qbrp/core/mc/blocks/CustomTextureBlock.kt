package org.qbrp.core.mc.blocks

import net.minecraft.util.Identifier

class CustomTextureBlock(val path: String, namespace: String = "qbrp"): ServerBlock {
    override fun getTexture(): Identifier {
        TODO("Not yet implemented")
    }
}