package org.qbrp.main.engine.music.plasmo.model.audio.playback

import org.qbrp.main.engine.music.plasmo.playback.player.VoicePlayerState

interface PlaybackSubscribe {
    fun subscribe(playerState: VoicePlayerState): Boolean
    fun unsubscribe(playerState: VoicePlayerState): Boolean
}