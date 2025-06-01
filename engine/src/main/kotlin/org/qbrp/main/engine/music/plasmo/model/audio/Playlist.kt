package org.qbrp.main.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlaybackSessionManager
import org.qbrp.main.engine.music.plasmo.view.PlaylistView
import org.qbrp.main.engine.music.plasmo.view.View
import org.qbrp.main.engine.music.plasmo.model.priority.Priority
import org.qbrp.main.engine.music.plasmo.model.selectors.Selector
import su.plo.voice.api.server.PlasmoVoiceServer

@JsonIgnoreProperties(ignoreUnknown = true)
open class Playlist(
    override var name: String,
    override var selector: Selector,
    override var priority: Priority,
    voiceServer: PlasmoVoiceServer
) : Playable(voiceServer) {

    @JsonIgnore
    override fun getView(): View {
        return PlaylistView(this)
    }

    override fun onUpdate() {
        sessionManager.doForSessions { player, session ->
            val cachedTrackIndex = session.queue.currentTrackIndex
            session.queue = queue.copy()
            session.queue.currentTrackIndex = cachedTrackIndex
            session.playable = this
        }
        save()
    }

}