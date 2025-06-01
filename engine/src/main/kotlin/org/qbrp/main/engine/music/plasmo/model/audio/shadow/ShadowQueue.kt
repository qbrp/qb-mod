package org.qbrp.main.engine.music.plasmo.model.audio.shadow

import org.qbrp.main.engine.music.plasmo.model.audio.Queue

class ShadowQueue(
    val originalPlaylistName: String): Queue() {
    override val tracks: MutableList<String>
        get() = storage.getPlayable(originalPlaylistName)!!.queue.tracks
}