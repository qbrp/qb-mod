package org.qbrp.engine.chat.ui.tasks

import net.minecraft.server.network.ServerPlayerEntity

data class Task(val player: String, val runnable: (ServerPlayerEntity) -> Unit) {
}