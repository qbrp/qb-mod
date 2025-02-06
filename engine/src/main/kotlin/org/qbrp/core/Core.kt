package org.qbrp.core

import net.fabricmc.api.ModInitializer
import org.qbrp.core.game.Game
import org.qbrp.core.game.events.Handlers
import org.qbrp.core.game.events.ServerReceivers

class Core: ModInitializer {
    companion object { const val MOD_ID = "qbrp" }

    override fun onInitialize() {
        Game.init()
        Handlers.registerBaseEvents()
    }
}