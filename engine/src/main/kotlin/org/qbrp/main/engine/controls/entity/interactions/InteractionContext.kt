package org.qbrp.main.engine.controls.entity.interactions

import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.system.PlayerIntention

data class InteractionContext(val intention: PlayerIntention, val call: InteractionRequestContext) {
    val player get() = call.player
}