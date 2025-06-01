package org.qbrp.main.core.mc.player.service

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.model.storage.GlobalStorage
import org.qbrp.main.core.mc.player.PlayerObject

class PlayerStorage: GlobalStorage<Long, PlayerObject>() {

    fun getByPlayer(serverPlayer: ServerPlayerEntity): PlayerObject {
        return getByPlayerName(serverPlayer.name.string)!!
    }

    fun getByPlayerName(name: String): PlayerObject? {
        return objects.find { it.name == name }
    }

}