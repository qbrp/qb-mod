package org.qbrp.engine.chat.addons.spy

import net.minecraft.server.network.ServerPlayerEntity

class SpyManager {
    private val ignoreSpyPlayersMap: MutableMap<ServerPlayerEntity, Boolean> = mutableMapOf()

    fun playerCanSpy(player: ServerPlayerEntity): Boolean {
        return ignoreSpyPlayersMap[player] != true
    }
}