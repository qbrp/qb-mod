package org.qbrp.core.mc.player.model

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.model.State
import org.qbrp.core.game.serialization.ObjectJsonField

class PlayerJsonField(id: Long,
                      val accountUuid: String,
                      state: State, name: String):
    ObjectJsonField(accountUuid.hashCode().toLong(), name, state, false) {

    fun upsertForPlayer(entity: ServerPlayerEntity) {
        name = entity.name.string
    }
}