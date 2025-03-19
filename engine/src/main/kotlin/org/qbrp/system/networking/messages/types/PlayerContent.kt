package org.qbrp.system.networking.messages.types

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.core.ServerCore

class PlayerContent(var player: PlayerEntity? = null): GenericContent(
    writeLambda = { buf -> buf.writeString(player?.name?.string) },
    convertLambda = { buf -> PlayerContent(
        ServerCore.server.playerManager.getPlayer(buf.readString())
    ) }
) {
    override fun getData(): PlayerEntity {
        return player!!
    }

    override fun setData(data: Any) {
        player = data as PlayerEntity
    }
}