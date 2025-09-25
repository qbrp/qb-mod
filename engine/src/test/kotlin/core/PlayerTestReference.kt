package core

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerReference
import java.util.UUID

data class PlayerTestReference(override val uuid: MinecraftPlayerId) : PlayerReference {
    companion object {
        fun random(): PlayerTestReference = PlayerTestReference(MinecraftPlayerId(UUID.randomUUID()))
    }
}