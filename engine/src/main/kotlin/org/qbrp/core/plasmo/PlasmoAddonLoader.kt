package org.qbrp.core.plasmo

import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.InjectPlasmoVoice
import su.plo.voice.api.server.PlasmoVoiceServer

open class PlasmoAddonLoader(val moduleName: String, val events: PlasmoEventListener) : AddonInitializer {
    private val logger = Loggers.get(moduleName, "plasmoLoader")

    @InjectPlasmoVoice
    lateinit var voiceServer: PlasmoVoiceServer

    override fun onAddonInitialize() {
        voiceServer.eventBus.register(this, events)
        logger.log("Интегрированный аддон модуля ${moduleName} загружен")
    }

    override fun onAddonShutdown() {
        logger.log("Интегрированный аддон ${moduleName} выгружен")
    }
}
