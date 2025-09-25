package org.qbrp.main.engine.players.model.service

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.core.game.storage.GlobalStorage
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.players.model.PlayerObject

open class PlayerStorage<T: PlayerObject>: GlobalStorage<T>() {
    @Deprecated("Использовать снапшот или ID")
    fun getByPlayer(serverPlayer: PlayerEntity): T {
        return getByPlayerName(serverPlayer.name.string)!!
    }

    fun getByPlayer(snapshot: PlayerSnapshot): T {
        return getByPlayerName(snapshot.username.value)!!
    }

    fun getByMinecraftId(id: MinecraftPlayerId): T? {
        return objects.find { it.minecraftPlayerId == id }
    }

    fun getByPlayerOrNull(serverPlayer: PlayerEntity): T? {
        return getByPlayerName(serverPlayer.name.string)
    }

    fun getByPlayerOrNull(serverPlayer: PlayerSnapshot): T? {
        return getByPlayerName(serverPlayer.username.value)
    }

    fun getByPlayerName(name: String): T? {
        return objects.find { it.username.value == name }
    }
}