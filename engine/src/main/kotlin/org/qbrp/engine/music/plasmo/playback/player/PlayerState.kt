package org.qbrp.engine.music.plasmo.playback.player

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer

class PlayerState(voiceServer: PlasmoVoiceServer, val player: ServerPlayerEntity) {
    companion object { val logger = Loggers.get("musicManager", "controller") }
    private var currentPlayable: Playable? = null

    val voicePlayer = voiceServer.playerManager
        .getPlayerByName(player.name.string)
        .orElseThrow()

    fun sync(playlist: Playable) {
        if (currentPlayable == playlist) { playlist.subscribe(this); return }
        if (currentPlayable?.unsubscribe(this) == true || currentPlayable == null) {
            currentPlayable = playlist.apply { subscribe(this@PlayerState) }
        }
    }

    fun desync() {
        if (currentPlayable != null && currentPlayable?.unsubscribe(this) == true) {
            logger.log("${player.name.string} расссинхронизирован")
            currentPlayable = null
        }
    }

    fun handleDisconnect() {
        currentPlayable?.killSession(voicePlayer)
    }
}