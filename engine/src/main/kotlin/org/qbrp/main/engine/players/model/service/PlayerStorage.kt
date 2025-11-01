package org.qbrp.main.engine.players.model.service

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.core.util.storage.GlobalStorage
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.engine.players.model.PlayerObject

open class PlayerStorage<T: PlayerObject>: GlobalStorage<T>() {
    @Deprecated("Использовать снапшот или ID")
    fun getByPlayer(serverPlayer: PlayerEntity): T {
        return getByPlayerName(serverPlayer.name.string)!!
    }

    fun getByMinecraftId(id: MinecraftPlayerId): T? {
        return objects.find { it.minecraftPlayerId == id }
    }

    fun getByPlayerOrNull(serverPlayer: PlayerEntity): T? {
        return getByPlayerName(serverPlayer.name.string)
    }

    fun getByPlayerName(name: String): T? {
        return objects.find { it.username.value == name }
    }
}