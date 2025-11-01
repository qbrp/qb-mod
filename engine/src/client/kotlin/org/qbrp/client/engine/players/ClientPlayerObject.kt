package org.qbrp.client.engine.players

import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.PlayerProfile
import org.qbrp.main.engine.players.model.PlayerStateImpl
import org.qbrp.main.core.util.PlayerId

class ClientPlayerObject(
    id: PlayerId,
    username: Username,
    val profile: PlayerProfile,
) : PlayerObject(
    username,
    profile.minecraftPlayerId,
    id,
    PlayerStateImpl()
)