package org.qbrp.main.core.mc.player.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.serialization.Identifiable

@Serializable
data class PlayerJsonField(val accountUuid: String,
                           val state: State,
                           var name: String): Identifiable {
    @Transient override val id: String = accountUuid
}