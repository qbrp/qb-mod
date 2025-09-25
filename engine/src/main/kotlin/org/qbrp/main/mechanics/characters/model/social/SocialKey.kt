package org.qbrp.main.mechanics.characters.model.social

import kotlinx.serialization.Serializable

@Serializable
data class SocialKey(val accountId: String, val characterId: Int)
