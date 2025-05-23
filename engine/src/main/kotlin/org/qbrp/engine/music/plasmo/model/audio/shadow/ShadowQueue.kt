package org.qbrp.engine.music.plasmo.model.audio.shadow

import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.MusicStorage
import org.qbrp.engine.music.plasmo.model.audio.Queue

class ShadowQueue(
    val originalPlaylistName: String): Queue() {
    override val tracks: MutableList<String>
        get() = storage.getPlayable(originalPlaylistName)!!.queue.tracks
}