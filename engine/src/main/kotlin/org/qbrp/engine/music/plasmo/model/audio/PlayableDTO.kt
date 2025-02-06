package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayableDTO(
    val name: String,
    val selector: Selector,
    val priority: Priority,
    val queue: Queue,
    val isManuallyDisabled: Boolean
)