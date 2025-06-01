package org.qbrp.main.core.mc.player.service

import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.serialization.Identifiable

@Serializable
data class PlayerJsonField(val accountUuid: String,
                           val state: State,
                           var name: String): Identifiable {
    override val id: Long = accountUuid.hashCode().toLong()
}