package org.qbrp.engine.music.plasmo.model.audio.playback

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.music.plasmo.playback.player.PlayerState

interface PlaybackSubscribe {
    fun subscribe(playerState: PlayerState): Boolean
    fun unsubscribe(playerState: PlayerState): Boolean
}