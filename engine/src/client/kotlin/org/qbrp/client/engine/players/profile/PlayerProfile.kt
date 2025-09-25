package org.qbrp.client.engine.players.profile

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.players.model.util.PlayerId

class PlayerProfile(val id: PlayerId, val username: Username, val minecraftPlayerId: MinecraftPlayerId)