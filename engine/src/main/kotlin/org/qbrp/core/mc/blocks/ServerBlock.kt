package org.qbrp.core.mc.blocks

import net.minecraft.util.Identifier

// TODO: Позже заменить на систему с компонентами
interface ServerBlock {

    fun getTexture(): Identifier

}