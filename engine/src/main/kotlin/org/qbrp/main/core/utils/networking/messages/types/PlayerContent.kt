package org.qbrp.main.core.utils.networking.messages.types

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.core.Core

class PlayerContent(var player: PlayerEntity? = null): GenericContent(
    writeLambda = { buf -> buf.writeString(player?.name?.string) },
    convertLambda = { buf -> PlayerContent(
        Core.server.playerManager.getPlayer(buf.readString())
    ) }
) {
    override fun getData(): PlayerEntity {
        return player!!
    }

    override fun setData(data: Any) {
        player = data as PlayerEntity
    }
}