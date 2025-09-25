package org.qbrp.main.engine.anticheat

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.isOp
import org.qbrp.main.engine.anticheat.AnticheatModule.Companion.RESTRICTED_MODS
import kotlin.collections.contains

class ModListHandler(private val opNotificator: OpNotificator) {
    context(mc: MinecraftContext)
    fun onModIds(player: PlayerSnapshot, modList: List<String>) {
        if (!player.isOp) {
            val restrictedMods = mutableListOf<String>()
            RESTRICTED_MODS.forEach {
                if (modList.contains<String>(it.id)) {
                    it.ifFounded(player, opNotificator)
                    restrictedMods.add(it.id)
                }
            }
            if (restrictedMods.isNotEmpty()) {
                opNotificator.notifyOperators("<red>[!]<reset> <gold>${player.username.value} зашел на сервер с ${restrictedMods.joinToString(", ")}</gold>")
            }
        }
    }
}