package org.qbrp.client.engine.players.profile

import org.qbrp.main.engine.players.model.util.PlayerId

interface PlayerProfileManager {
    fun getProfile(id: PlayerId): PlayerProfile?
    fun requireProfile(id: PlayerId): PlayerProfile {
        return getProfile(id) ?: throw NullPointerException("Profile $id not found")
    }
    fun addOrUpdateProfile(profile: PlayerProfile)
}