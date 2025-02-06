package org.qbrp.engine.client.render

import org.qbrp.engine.client.render.game.chat.PlayerIconRenderer
import org.qbrp.engine.client.render.hud.Hud

class Render {
    companion object { val HUD: Hud = Hud() }

    fun initialize() {
        PlayerIconRenderer().initialize()
    }

}