package org.qbrp.engine.chat.ui.model

import net.minecraft.server.network.ServerPlayerEntity

interface Element {
    fun getText(): String
    fun build(player: ServerPlayerEntity)
}