package org.qbrp.main.engine.players.characters.entity

import kotlinx.serialization.Serializable

@Serializable
data class SocialKey(val accountId: String, val characterId: Int)