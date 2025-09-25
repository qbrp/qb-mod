package org.qbrp.client.engine.players

import org.qbrp.client.engine.players.profile.PlayerProfile
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.PlayerStateImpl
import org.qbrp.main.engine.players.model.util.PlayerId

class ClientPlayerObject(id: PlayerId,
                         username: Username,
                         val profile: PlayerProfile,
): PlayerObject(username, profile.minecraftPlayerId, id, PlayerStateImpl())