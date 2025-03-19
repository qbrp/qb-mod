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

@JsonIgnoreProperties(ignoreUnknown = true)
class Shadow(
    val originalName: String,
    override var name: String,
    override var selector: Selector,
    override var priority: Priority,
    voiceServer: PlasmoVoiceServer
) : Playable(voiceServer), KoinComponent {
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