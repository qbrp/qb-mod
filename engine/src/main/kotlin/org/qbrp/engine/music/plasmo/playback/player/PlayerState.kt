package org.qbrp.engine.music.plasmo.playback.player

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.system.utils.log.Loggers

class PlayerState(val player: ServerPlayerEntity) {
    companion object { val logger = Loggers.get("musicManager", "controller") }
    private var currentPlayable: Playable? = null

    val voicePlayer = Engine.musicManagerModule.getVoiceServer().playerManager
        .getPlayerByName(player.name.string)
        .orElseThrow()

    fun sync(playlist: Playable) {
        if (currentPlayable != playlist) {
            currentPlayable?.unsubscribe(this)
            currentPlayable = playlist.apply { subscribe(this@PlayerState) }
        }
    }

    fun desync() {
        if (currentPlayable != null) {
            logger.log("${player.name.string} расссинхронизирован")
            currentPlayable?.unsubscribe(this)
            currentPlayable = null
        }
    }
}