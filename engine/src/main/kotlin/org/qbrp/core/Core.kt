package org.qbrp.core

import net.fabricmc.api.ModInitializer
import org.qbrp.core.mc.Game
import org.qbrp.core.mc.events.Handlers

class Core: ModInitializer {
    companion object { const val MOD_ID = "qbrp" }

    override fun onInitialize() {
        Game.init()
    }
}