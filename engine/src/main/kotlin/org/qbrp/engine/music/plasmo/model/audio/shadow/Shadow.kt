package org.qbrp.engine.music.plasmo.model.audio.shadow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.koin.core.component.KoinComponent
import org.qbrp.engine.music.plasmo.view.PlaylistView
import org.qbrp.engine.music.plasmo.view.View
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.PlayableDTO
import org.qbrp.engine.music.plasmo.model.audio.Queue
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import su.plo.voice.api.server.PlasmoVoiceServer
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSessionManager

@JsonIgnoreProperties(ignoreUnknown = true)
class Shadow(
    val originalName: String,
    name: String,
    selector: Selector,
    priority: Priority,
    voiceServer: PlasmoVoiceServer
) : Playlist(name, selector, priority, voiceServer), KoinComponent {

    @JsonIgnore override var queue: Queue = get(parameters = { parametersOf(originalName) })

    override fun getView(): View {
        return PlaylistView(this)
    }

    override fun toDTO(): PlayableDTO {
        return PlayableDTO(
            type = "shadow",
            name = this.name,
            selector = this.selector,
            priority = this.priority,
            queue = this.queue,
            isManuallyDisabled = this.isManuallyDisabled,
        )
    }

}