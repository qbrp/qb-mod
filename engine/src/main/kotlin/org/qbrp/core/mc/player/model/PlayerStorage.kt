package org.qbrp.core.mc.player.model

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World
import org.qbrp.core.game.model.storage.GlobalStorage
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.mc.player.PlayerObject

class PlayerStorage: GlobalStorage<Long, PlayerObject>(), Tick<ServerWorld> {

    fun getByPlayer(serverPlayer: ServerPlayerEntity): PlayerObject {
        return getByPlayerName(serverPlayer.name.string)!!
    }

    fun getByPlayerName(name: String): PlayerObject? {
        return objects.find { it.name == name }
    }

    override fun tick(context: ServerWorld) {
        tickAll(context)
    }
}