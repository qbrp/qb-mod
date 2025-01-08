package org.qbrp.plasmo

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.ServerCore
import org.qbrp.plasmo.contoller.AddonStorage
import org.qbrp.plasmo.contoller.PlayerSelectionHandler
import org.qbrp.plasmo.contoller.player.SourceManager
import org.qbrp.plasmo.contoller.lavaplayer.AudioManager
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.AddonLoaderScope
import su.plo.voice.api.addon.InjectPlasmoVoice
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.event.EventSubscribe
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.event.connection.UdpClientConnectEvent
import su.plo.voice.api.server.event.connection.UdpClientDisconnectedEvent

@Addon(
    // An addon id must start with a lowercase letter and may contain only lowercase letters, digits, hyphens, and underscores. // It should be between 4 and 32 characters long.
    id = "pv-addon-music-manager",
    name = "Music Manager",
    version = "1.0.0",
    authors = ["lain1wakura"],
    scope = AddonLoaderScope.ANY
)
class Addon(val server: MinecraftServer) : AddonInitializer {
    private val logger = Loggers.get("plasmo")
    lateinit var sourceManager: SourceManager
    lateinit var playerSelectionHandler: PlayerSelectionHandler

    @InjectPlasmoVoice
    private lateinit var voiceServer: PlasmoVoiceServer

//    fun test() {
//        AddonStorage.addTrack("Made in Abyss", "https://kappa.vgmsite.com/soundtracks/made-in-abyss-ost/pmxedjjeon/1-01%20Made%20in%20Abyss.mp3").apply {
//            //endTimestamp = 30.0
//            setFadeTime(6.0)
//        }
//        AddonStorage.getDefaultPlaylist().apply {
//            addTrack("Made in Abyss")
//            play()
//        }
//    }

    override fun onAddonInitialize() {
        registerSourceLine()
        AudioManager.registerSources()
        sourceManager = SourceManager()
        SourceManager.voiceServer = voiceServer
        playerSelectionHandler = PlayerSelectionHandler(sourceManager)
        playerSelectionHandler.startHandling()
        AddonStorage.addDefaultPlaylist()
        voiceServer.eventBus.register(this, EventListener())
        logger.success("Аддон Music-Manager инициализирован.")
        //test()
    }

    override fun onAddonShutdown() {
        logger.log("Аддон Music-Manager выгружен.")
    }

    fun registerSourceLine()
    {
        val sourceLine = voiceServer.sourceLineManager.createBuilder(
            this,
            "music",
            "Музыка и звуковые эффекты",
            "plasmovoice:textures/icons/speaker_priority.png",
            10,
        ).build()
    }
    inner class EventListener {

        @EventSubscribe
        fun onClientConnected(event: UdpClientConnectEvent) {
            ServerCore.plasmoAddon.sourceManager.addPlayer(server.playerManager.getPlayer(event.connection.player.instance.uuid) as ServerPlayerEntity)
        }

        @EventSubscribe
        fun onClientDisconnected(event: UdpClientDisconnectedEvent) {
            ServerCore.plasmoAddon.sourceManager.removePlayer(server.playerManager.getPlayer(event.connection.player.instance.uuid) as ServerPlayerEntity)
        }
    }
}
