package org.qbrp.client.render.heads

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.engine.players.model.PlayerProfile

interface PlayerHeadsRenderer {
    fun renderHeadTexture(ctx: Draw2dContext, player: PlayerProfile, x1: Int, y1: Int, scale: Float)
}