package org.qbrp.core

import config.ClientConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.qbrp.core.mc.Game

class Core: ModInitializer {
    companion object { const val MOD_ID = "qbrp" }

    override fun onInitialize() {
        Game.init()
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            eu.midnightdust.lib.config.MidnightConfig.init(MOD_ID, ClientConfig::class.java)
        }
    }
}