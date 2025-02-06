package org.qbrp.engine.music.plasmo

import net.minecraft.server.MinecraftServer
import org.qbrp.core.plasmo.PlasmoAddonLoader
import org.qbrp.core.plasmo.PlasmoEventListener
import su.plo.voice.api.addon.AddonLoaderScope
import su.plo.voice.api.addon.annotation.Addon

@Addon(
    id = "pv-addon-music-manager",
    name = "Music Manager",
    version = "1.0.0",
    authors = ["lain1wakura"],
    scope = AddonLoaderScope.ANY
)
class MusicAddonLoader(moduleName: String, val server: MinecraftServer, events: PlasmoEventListener) : PlasmoAddonLoader(moduleName, events) {
    override fun onAddonInitialize() {
        super.onAddonInitialize()
        voiceServer.sourceLineManager.createBuilder(
            this,
            "music",
            "Музыка и звуковые эффекты",
            "plasmovoice:textures/icons/speaker_priority.png",
            10,
        ).build()
    }
}
