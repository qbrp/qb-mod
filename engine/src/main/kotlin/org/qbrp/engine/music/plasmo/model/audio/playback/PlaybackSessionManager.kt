package org.qbrp.engine.music.plasmo.model.audio.playback

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.music.plasmo.playback.player.PlayerState
import su.plo.voice.api.server.player.VoicePlayer
import su.plo.voice.api.server.player.VoiceServerPlayer

interface PlaybackSessionManager: PlaybackSubscribe {
    fun destroySession(playerState: VoicePlayer)
    fun doForSessions(block: (VoiceServerPlayer, PlayerSession) -> Unit)
    suspend fun doForSessionsAsync(block: suspend (VoiceServerPlayer, PlayerSession) -> Unit)
    fun getSession(player: ServerPlayerEntity): PlayerSession?
}