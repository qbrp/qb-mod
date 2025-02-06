package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.*
import org.qbrp.system.utils.log.Loggers
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.engine.music.plasmo.controller.view.PlaylistView
import org.qbrp.engine.music.plasmo.controller.view.View
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import su.plo.voice.api.server.PlasmoVoiceServer

@JsonIgnoreProperties(ignoreUnknown = true)
class Playlist(
    override var name: String,
    override var selector: Selector,
    override var priority: Priority,
    voiceServer: PlasmoVoiceServer
) : Playable(voiceServer) {

    override fun getView(): View {
        return PlaylistView(this)
    }

    companion object {
        fun buildFromDTO(dto: PlayableDTO, voiceServer: PlasmoVoiceServer): Playable {
            return Playlist(dto.name, dto.selector, dto.priority, voiceServer).apply { initQueue(dto.queue.copy()); isManuallyDisabled = dto.isManuallyDisabled  }
        }
    }


}