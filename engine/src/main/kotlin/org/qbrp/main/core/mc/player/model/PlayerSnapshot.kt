package org.qbrp.main.core.mc.player.model

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.world.location.Position

interface PlayerSnapshot: PlayerReference {
    val username: Username
    val pos: Position
    val entity: EntitySnapshot
    val serverState: ServerStateSnapshot
    val attributes: PlayerAttributes
}