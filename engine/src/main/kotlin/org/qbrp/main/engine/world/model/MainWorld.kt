package org.qbrp.main.engine.world.model

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

class MainWorld(private val playerStorage: ServerPlayerStorage, private val minecraftPlayerManger: MinecraftPlayerManager) : World {
    lateinit var mcWorld: ServerWorld
    override val players: List<ServerPlayerObject> get() = playerStorage.filter { it.getEntity(minecraftPlayerManger).world === mcWorld }


}