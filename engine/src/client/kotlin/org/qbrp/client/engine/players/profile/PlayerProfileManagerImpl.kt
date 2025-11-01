package org.qbrp.client.engine.players.profile

import org.qbrp.main.engine.players.model.PlayerProfile
import org.qbrp.main.core.util.PlayerId

class PlayerProfileManagerImpl(): PlayerProfileManager {
    private val serverProfiles: MutableMap<PlayerId, PlayerProfile> = mutableMapOf()

    override fun getProfile(id: PlayerId): PlayerProfile? {
        return serverProfiles[id]
    }

    override fun addOrUpdateProfile(profile: PlayerProfile) {
        serverProfiles[profile.id] = profile
    }
}