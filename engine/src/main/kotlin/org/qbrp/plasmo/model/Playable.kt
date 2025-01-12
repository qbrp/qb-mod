package org.qbrp.plasmo.model

import org.qbrp.plasmo.controller.view.View
import org.qbrp.plasmo.model.audio.Track
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.Selector

abstract class Playable() {
    abstract var selector: Selector
    abstract var priority: Priority
    abstract var cycle: Int

    abstract fun getCurrentTrack(): Track
    abstract fun play()
    abstract fun stop()
    abstract fun getView(): View
}