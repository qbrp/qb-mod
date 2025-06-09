package org.qbrp.main.core.mc.player.service

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.storage.GlobalStorage
import org.qbrp.main.core.mc.player.PlayerObject

open class PlayerStorage<T: PlayerObject>: GlobalStorage<T>() {
    fun getByPlayer(serverPlayer: ServerPlayerEntity): T {
        return getByPlayerName(serverPlayer.name.string)!!
    }

    fun getByPlayerOrNull(serverPlayer: ServerPlayerEntity): T? {
        return getByPlayerName(serverPlayer.name.string)
    }

    fun getByPlayerName(name: String): T? {
        return objects.find { it.entityName == name }
    }

}