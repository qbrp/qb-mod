package org.qbrp.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity

abstract class Selector() {
   companion object { const val name: String = "selector" }
   abstract val params: MutableMap<String, String>
   abstract fun match(player: ServerPlayerEntity): Boolean
}